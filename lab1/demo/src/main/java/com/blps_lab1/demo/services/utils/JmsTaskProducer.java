package com.blps_lab1.demo.services.utils;

import com.blps_lab1.demo.dto.TaskMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import jakarta.jms.BytesMessage;
import java.nio.charset.StandardCharsets;

@Service
public class JmsTaskProducer {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JmsTaskProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendToQueue(String queueName, TaskMessage message) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(message);
            byte[] bytes = jsonPayload.getBytes(StandardCharsets.UTF_8);

            jmsTemplate.send(queueName, session -> {
                BytesMessage bytesMessage = session.createBytesMessage();
                bytesMessage.writeBytes(bytes);
                return bytesMessage;
            });

            System.out.println(">>> [JMS XA TRACE] Сообщение буферизировано в XA-контексте для очереди: " + queueName);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка отправки сообщения через JMS XA", e);
        }
    }
}