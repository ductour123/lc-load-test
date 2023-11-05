package com.saltlux.livechat.load;

import com.saltlux.livechat.load.service.TotalLogic;
import com.saltlux.livechat.load.service.WsTotalLogic;
import com.saltlux.livechat.load.util.ClientInfo;
import com.saltlux.livechat.load.util.LCStompFrameHandler;
import com.saltlux.livechat.load.util.StompSessionHandlerUtil;
import com.saltlux.livechat.load.util.WsSessionDTO;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
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
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
@AllArgsConstructor
public class Application {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        WsTotalLogic press = context.getBean(WsTotalLogic.class);
        press.startPress(); // 시작 유저 수, 테스트 시간, 증감 유저 수, 증감 주기

//        String URL = "ws://localhost:8005/websocket";
//        String URL = "ws://13.21.34.11:8005/websocket";
//
////        WebSocketClient wsClient = new StandardWebSocketClient();
//        List<Transport> transports = new ArrayList<>(2);
//        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
//        transports.add(new RestTemplateXhrTransport());
//        SockJsClient wsClient = new SockJsClient(transports);
//
//
//        WebSocketStompClient stompClient = new WebSocketStompClient(wsClient);
//        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
////        stompClient.setTaskScheduler(new ConcurrentTaskScheduler());
//
//        StompSessionHandler sessionHandler = new StompSessionHandlerUtil();
////        stompClient.connect(URL, sessionHandler);
//
//        StompSession session = stompClient.connect(URL, sessionHandler).get();
//
//        // Create uuid to as livechatSessionId, after listen
//        String livechatSessionId = UUID.randomUUID().toString();
//
//        Logger logger = LogManager.getLogger(Application.class);
//        // subcribe to topic socket server
//        session.subscribe("/conversation/" + livechatSessionId, new LCStompFrameHandler(null));
//        logger.info("Subscribed to /conversation/" + livechatSessionId);
//
//        ClientInfo clientInfo = new ClientInfo();
//        clientInfo.setLivechatId("9eef26c1-51ea-4ecb-abec-632bf49138fa");
//        session.send("/livechat/startConversation/" + livechatSessionId, clientInfo);
//
//        WsSessionDTO wsSessionDTO = new WsSessionDTO();
//        wsSessionDTO.setMessage("승차권 예약 및 환불 안내");
//        session.send("/livechat/sendMessage/" + livechatSessionId, wsSessionDTO);
//
//        wsSessionDTO.setMessage("예약 편의서비스");
//        session.send("/livechat/sendMessage/" + livechatSessionId, wsSessionDTO);

        //request agent
//        session.send("/livechat/requestAgent/" + livechatSessionId, null);
//
//        wsSessionDTO.setMessage("Indian startups today are MNCs of tomorrow, says PM");
//        session.send("/livechat/sendMessage/" + livechatSessionId, wsSessionDTO);
//
//        wsSessionDTO.setMessage("Amazon launches essentials and grocery delivery in Kolkata");
//        session.send("/livechat/sendMessage/" + livechatSessionId, wsSessionDTO);
//
//        // stop Conversation
//        wsSessionDTO.setSessionId(livechatSessionId);
//        session.send("/livechat/stopConversation/" + livechatSessionId, wsSessionDTO);


//        new Scanner(System.in).nextLine();

    }
}
