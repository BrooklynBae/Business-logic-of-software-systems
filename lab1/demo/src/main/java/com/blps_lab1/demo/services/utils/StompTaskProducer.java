package com.blps_lab1.demo.services.utils;

import com.blps_lab1.demo.dto.TaskMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

@Service
public class StompTaskProducer {

    private StompSession stompSession;
    private WebSocketStompClient stompClient;

    @Value("${spring.artemis.stomp.login}")
    private String stompLogin;

    @Value("${spring.artemis.stomp.passcode}")
    private String stompPasscode;

    @PostConstruct
    public void init() {
        this.stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        this.stompClient.setMessageConverter(new JacksonJsonMessageConverter());
        ensureConnected();
    }

    private synchronized void ensureConnected() {
        if (stompSession != null && stompSession.isConnected()) {
            return;
        }
        try {
            StompHeaders connectHeaders = new StompHeaders();
            connectHeaders.add("login", stompLogin);
            connectHeaders.add("passcode", stompPasscode);

            WebSocketHttpHeaders webSocketHeaders = new WebSocketHttpHeaders();

            this.stompSession = stompClient.connectAsync(
                    "ws://localhost:61613/stomp",
                    webSocketHeaders,
                    connectHeaders,
                    new StompSessionHandlerAdapter() {}
            ).get();
        } catch (Exception e) {
            System.err.println("STOMP connection failed or broker unreachable: " + e.getMessage());
        }
    }

    public void sendToQueue(String queueName, TaskMessage message) {
        try {
            ensureConnected();

            if (stompSession != null && stompSession.isConnected()) {
                StompHeaders headers = new StompHeaders();
                headers.setDestination(queueName);
                headers.add("_type", "com.blps_lab1.demo.dto.TaskMessage");

                stompSession.send(headers, message);
                System.out.println(">>> [STOMP TRACE] Сообщение успешно отправлено в брокер по адресу: " + queueName);
            } else {
                throw new IllegalStateException("STOMP session is still not available after reconnection attempt");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error sending STOMP message", e);
        }
    }
}