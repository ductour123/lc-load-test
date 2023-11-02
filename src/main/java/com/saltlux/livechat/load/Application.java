package com.saltlux.livechat.load;

import com.saltlux.livechat.load.service.TotalLogic;
import com.saltlux.livechat.load.util.StompSessionHandlerUtil;
import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
@AllArgsConstructor
public class Application {
    public static void main(String[] args)  {
//        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
//        TotalLogic press = context.getBean(TotalLogic.class);
//        press.startPress(); // 시작 유저 수, 테스트 시간, 증감 유저 수, 증감 주기

        String URL = "ws://localhost:8005/websocket";
//        String URL = "ws://13.21.34.11:8005/websocket";


//        WebSocketClient wsClient = new StandardWebSocketClient();
        List<Transport> transports = new ArrayList<>(2);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        transports.add(new RestTemplateXhrTransport());
        SockJsClient wsClient = new SockJsClient(transports);


        WebSocketStompClient stompClient = new WebSocketStompClient(wsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
//        stompClient.setTaskScheduler(new ConcurrentTaskScheduler());

        StompSessionHandler sessionHandler = new StompSessionHandlerUtil();
        stompClient.connect(URL, sessionHandler);


        new Scanner(System.in).nextLine();

    }
}
