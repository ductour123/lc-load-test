package com.saltlux.livechat.load.util;

import com.saltlux.livechat.load.Application;
import com.saltlux.livechat.load.config.LivechatEndpoint;
import com.saltlux.livechat.load.config.TestConfig;
import com.saltlux.livechat.load.vo.ResultShare;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.*;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@Service
@Slf4j
@AllArgsConstructor
public class WsSessionUtil {
    public StompSession wsSession;
//    private RestTemplate restTemplate;
    private LivechatEndpoint livechatEndpoint;
    private TestConfig testConfig;

    public WsSessionUtil() throws ExecutionException, InterruptedException {
        createWsSession();
    }

    public void createWsSession() throws ExecutionException, InterruptedException {
        List<Transport> transports = new ArrayList<>(2);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        transports.add(new RestTemplateXhrTransport());
        SockJsClient wsClient = new SockJsClient(transports);

        WebSocketStompClient stompClient = new WebSocketStompClient(wsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        this.wsSession = stompClient.connect(livechatEndpoint.getUrlConnectWs(), new StompSessionHandlerUtil()).get();
    }


    public String startConversation(ResultShare resultShare) {
        // Create uuid to as livechatSessionId, after listen
        String livechatSessionId = UUID.randomUUID().toString();
        // subcribe to topic socket server
        wsSession.subscribe("/conversation/" + livechatSessionId, new LCStompFrameHandler(resultShare)).;
        // log.info("Subscribed to /conversation/" + livechatSessionId);

        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setLivechatId(testConfig.getLivechatId());
        wsSession.send("/livechat/startConversation/" + livechatSessionId, clientInfo);

        return livechatSessionId;
    }

    public void sendMessage(String message, String conversationId, Consumer<RestTemplateUtil.CallResult> onComplete) {
        RestTemplateUtil.CallResult callResult = new RestTemplateUtil.CallResult(RestTemplateUtil.CallStatus.Success, 0, null);
        StopWatch sw = StopWatch.createStarted();
        try {
            String url = livechatEndpoint.getSendMessageUrl(conversationId);
            restTemplate.postForEntity(url, message.getBytes(StandardCharsets.UTF_8), String.class);
        } catch (Exception ex) {
            callResult.setStatus(RestTemplateUtil.CallStatus.Error);
            callResult.setException(ex);
        } finally {
            sw.stop();
            callResult.setTook(sw.getTime());
            onComplete.accept(callResult);
        }
    }

    public void closeConversatioId(String conversationId, ResultShare resultshare) {
        log.debug("Close Conversation ID : {}", conversationId);
        HttpHeaders getHeader = new HttpHeaders();
        HttpEntity<String> getEntity = new HttpEntity<String>(getHeader);

        try{
            String url = livechatEndpoint.getStopConversationUrl(conversationId);
            ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET, getEntity, String.class);

            if(result.getStatusCode() != HttpStatus.OK) {
                log.info("close conversation error: {}", result.getStatusCode());
            }

        } catch(Exception e){
            log.info("close conversation error: {}", e.getClass().getSimpleName() );
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CallResult {
        RestTemplateUtil.CallStatus status;
        long took;
        Exception exception;
    }

    public enum CallStatus {
        Error,
        Success
    }
}
