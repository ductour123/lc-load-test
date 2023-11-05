package com.saltlux.livechat.load.util;

import com.saltlux.livechat.load.config.LivechatEndpoint;
import com.saltlux.livechat.load.config.TestConfig;
import com.saltlux.livechat.load.vo.WsResultShare;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class WsSessionUtil {
    public StompSession wsSession;
    public LivechatEndpoint livechatEndpoint;
    public TestConfig testConfig;
    public List<String> userAgentsData;
    public List<String> citiesData;
    public List<String> devicesData;
    public List<String> osData;

//    public WsSessionUtil() throws ExecutionException, InterruptedException {
//        createWsSession();
//    }

    public void createWsSession() throws ExecutionException, InterruptedException {
        List<Transport> transports = new ArrayList<>(2);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        transports.add(new RestTemplateXhrTransport());
        SockJsClient wsClient = new SockJsClient(transports);

        WebSocketStompClient stompClient = new WebSocketStompClient(wsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        this.wsSession = stompClient.connect(livechatEndpoint.getUrlConnectWs(), new StompSessionHandlerUtil()).get();
    }

    public String startConversation(WsResultShare resultShare) {
        // Create uuid to as livechatSessionId, after listen
        String livechatSessionId = UUID.randomUUID().toString();
        // subcribe to topic socket server
        wsSession.subscribe("/conversation/" + livechatSessionId, new LCStompFrameHandler(resultShare));
        // log.info("Subscribed to /conversation/" + livechatSessionId);

        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setLivechatId(testConfig.getLivechatId());
        // set random value of infos
        clientInfo.setCity(this.getRandomCity());
        clientInfo.setBrowser(this.getRandomUserAgent());
        clientInfo.setDevice(this.getRandomDevice());
        clientInfo.setOs(this.getRandomOs());

        wsSession.send("/livechat/startConversation/" + livechatSessionId, clientInfo);

        return livechatSessionId;
    }

    public void sendMessage(String message, String conversationId) {

        WsSessionDTO wsSessionDTO = new WsSessionDTO();
        wsSessionDTO.setMessage(message);
        wsSession.send("/livechat/sendMessage/" + conversationId, wsSessionDTO);

    }

    public void requestAgent(String conversationId) {
        //request agent
        wsSession.send("/livechat/requestAgent/" + conversationId, null);
    }

    public void closeConversatioId(String conversationId, WsResultShare resultshare) {

        log.debug("Close Conversation ID : {}", conversationId);
        // stop Conversation
        WsSessionDTO wsSessionDTO = new WsSessionDTO();
        wsSessionDTO.setSessionId(conversationId);
        wsSession.send("/livechat/stopConversation/" + conversationId, wsSessionDTO);

    }

    private String getRandomUserAgent() {
        int rdSentIdx = SystemUtil.getRandomNumber(0, this.userAgentsData.size() - 1);
        return this.userAgentsData.get(rdSentIdx);
    }

    private String getRandomOs() {
        int rdSentIdx = SystemUtil.getRandomNumber(0, this.osData.size() - 1);
        return this.osData.get(rdSentIdx);
    }

    private String getRandomDevice() {
        int rdSentIdx = SystemUtil.getRandomNumber(0, this.devicesData.size() - 1);
        return this.devicesData.get(rdSentIdx);
    }

    private String getRandomCity() {
        int rdSentIdx = SystemUtil.getRandomNumber(0, this.citiesData.size() - 1);
        return this.citiesData.get(rdSentIdx);
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
