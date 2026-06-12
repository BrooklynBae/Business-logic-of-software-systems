package com.blps_lab1.demo.config;

import org.apache.activemq.artemis.jms.client.ActiveMQXAConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import jakarta.jms.ConnectionFactory;

@Configuration
public class XaTransactionConfig {

    @Value("${spring.artemis.broker-url}")
    private String brokerUrl;

    @Value("${spring.artemis.user}")
    private String artemisUser;

    @Value("${spring.artemis.password}")
    private String artemisPassword;

    @Bean
    public ConnectionFactory jmsConnectionFactory() {
        try {
            ActiveMQXAConnectionFactory factory = new ActiveMQXAConnectionFactory();
            factory.setBrokerURL(brokerUrl);
            factory.setUser(artemisUser);
            factory.setPassword(artemisPassword);
            return factory;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Artemis XA ConnectionFactory", e);
        }
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
            ConnectionFactory jmsConnectionFactory,
            PlatformTransactionManager transactionManager) {

        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(jmsConnectionFactory);
        factory.setTransactionManager(transactionManager);
        factory.setSessionTransacted(true);
        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory jmsConnectionFactory) {
        JmsTemplate template = new JmsTemplate(jmsConnectionFactory);
        template.setSessionTransacted(true);
        return template;
    }
}