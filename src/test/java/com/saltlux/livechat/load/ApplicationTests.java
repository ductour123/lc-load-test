package com.saltlux.livechat.load;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@SpringBootTest
public class ApplicationTests {
    @Test
    void contextLoads() {
    }

    WebSocketClient webSocketClient = new StandardWebSocketClient();
    WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
//    stompClient.setMessageConverter(new StringMessageConverter());
//    stompClient.setTaskScheduler(taskScheduler); // for heartbeats



//    WebSocketClient client = new StandardWebSocketClient();
//
//    WebSocketStompClient stompClient = new WebSocketStompClient(client);
//    stompClient.setMessageConverter(new MappingJackson2MessageConverter());

//    StompSessionHandler sessionHandler = new MyStompSessionHandler();
//    stompClient.connect(URL, sessionHandler);

}
