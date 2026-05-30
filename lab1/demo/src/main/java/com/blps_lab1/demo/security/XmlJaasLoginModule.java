package com.blps_lab1.demo.security;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.io.File;
import java.security.Principal;
import java.util.*;

public class XmlJaasLoginModule implements LoginModule {

    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map<String, ?> options;

    private String username;
    private boolean loginSucceeded = false;

    private final Set<Principal> principalsToCommit = new HashSet<>();

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler,
                           Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.options = options;
    }

    @Override
    public boolean login() throws LoginException {
        NameCallback nameCallback = new NameCallback("username");
        PasswordCallback passwordCallback = new PasswordCallback("password", false);

        try {
            callbackHandler.handle(new Callback[]{nameCallback, passwordCallback});
            username = nameCallback.getName();
            String password = (passwordCallback.getPassword() != null) ? new String(passwordCallback.getPassword()) : "";

            String xmlPath = (String) options.get("xmlFilePath");
            if (xmlPath == null) {
                throw new LoginException("xmlFilePath option is missing in jaas.config");
            }

            return validateUserInXml(xmlPath, username, password);
        } catch (Exception e) {
            throw new LoginException("JAAS XML Authentication failed: " + e.getMessage());
        }
    }

    private boolean validateUserInXml(String path, String user, String pass) throws Exception {
        File xmlFile = new File(path);
        if (!xmlFile.exists()) {
            throw new LoginException("XML users file not found at: " + xmlFile.getAbsolutePath());
        }

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);
        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName("user");
        for (int i = 0; i < nList.getLength(); i++) {
            Node node = nList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                if (element.getAttribute("username").equals(user) &&
                        element.getAttribute("password").equals("{noop}" + pass)) {

                    loginSucceeded = true;

                    NodeList roles = element.getElementsByTagName("role");
                    for (int j = 0; j < roles.getLength(); j++) {
                        String roleName = ((Element) roles.item(j)).getAttribute("name");
                        principalsToCommit.add(new JaasRolePrincipal(roleName));
                    }

                    NodeList auths = element.getElementsByTagName("authority");
                    for (int j = 0; j < auths.getLength(); j++) {
                        String authName = ((Element) auths.item(j)).getAttribute("name");
                        principalsToCommit.add(new JaasAuthorityPrincipal(authName));
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean commit() throws LoginException {
        if (!loginSucceeded) {
            return false;
        }
        subject.getPrincipals().addAll(principalsToCommit);
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        if (!loginSucceeded) {
            return false;
        }
        logout();
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        subject.getPrincipals().removeAll(principalsToCommit);
        principalsToCommit.clear();
        loginSucceeded = false;
        return true;
    }
}