package com.blps_lab1.demo.config;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import jakarta.jms.ConnectionFactory;

@Configuration
@EnableJms
public class JmsConfig {

    @Value("${spring.artemis.broker-url}")
    private String brokerUrl;

    @Value("${spring.artemis.user}")
    private String user;

    @Value("${spring.artemis.password}")
    private String password;

    @Bean
    public ConnectionFactory jmsConnectionFactory() {
        try {
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
            factory.setBrokerURL(brokerUrl);
            factory.setUser(user);
            factory.setPassword(password);
            return factory;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Artemis ConnectionFactory", e);
        }
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory jmsConnectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(jmsConnectionFactory);
        return factory;
    }
}