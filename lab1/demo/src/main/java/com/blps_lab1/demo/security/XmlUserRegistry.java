package com.blps_lab1.demo.security;

import com.blps_lab1.demo.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.List;

@Component
public class XmlUserRegistry {

    private final String xmlFilePath;

    public XmlUserRegistry(@Value("${app.security.xml-realm-path}") String xmlFilePath) {
        this.xmlFilePath = xmlFilePath;
    }

    public synchronized void registerUserInXml(String username, String rawPassword, List<String> roles, List<String> authorities) {
        try {
            File xmlFile = new File(xmlFilePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            doc.getDocumentElement().normalize();
            Element root = doc.getDocumentElement();

            NodeList nList = doc.getElementsByTagName("user");
            for (int i = 0; i < nList.getLength(); i++) {
                Element element = (Element) nList.item(i);
                if (element.getAttribute("username").equals(username)) {
                    throw new BadRequestException("User with username '" + username + "' already exists");
                }
            }

            Element newUser = doc.createElement("user");
            newUser.setAttribute("username", username);
            newUser.setAttribute("password", "{noop}" + rawPassword);

            if (roles != null) {
                for (String role : roles) {
                    Element roleEl = doc.createElement("role");
                    roleEl.setAttribute("name", role);
                    newUser.appendChild(roleEl);
                }
            }

            if (authorities != null) {
                for (String auth : authorities) {
                    Element authEl = doc.createElement("authority");
                    authEl.setAttribute("name", auth);
                    newUser.appendChild(authEl);
                }
            }

            root.appendChild(newUser);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xmlFile);
            transformer.transform(source, result);

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to write new user to XML Security Realm", e);
        }
    }
}