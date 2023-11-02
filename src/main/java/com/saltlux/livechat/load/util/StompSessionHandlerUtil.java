package com.saltlux.livechat.load.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;

public class StompSessionHandlerUtil extends StompSessionHandlerAdapter {

    private Logger logger = LogManager.getLogger(StompSessionHandlerUtil.class);

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        logger.info("New session established : " + session.getSessionId());

        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setLivechatId("9eef26c1-51ea-4ecb-abec-632bf49138fa");

        // subcribe to topic socket server
        session.subscribe("/conversation/" + session.getSessionId(), this);
        logger.info("Subscribed to /conversation/" + session.getSessionId());

        session.send("/livechat/startConversation/" + session.getSessionId(), clientInfo);

        SessionWsDTO sessionWsDTO = new SessionWsDTO();
        sessionWsDTO.setMessage("I want learn more about the policy");
        session.send("/livechat/sendMessage/" + session.getSessionId(), sessionWsDTO);

//        sessionWsDTO.setMessage("No, I just want see the product comparison chart");
//        session.send("/livechat/sendMessage/" + session.getSessionId(), sessionWsDTO);

        // stop Conversation
        sessionWsDTO.setSessionId(session.getSessionId());
        session.send("/livechat/stopConversation/" + session.getSessionId(), sessionWsDTO);

    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        logger.error("Got an exception", exception);
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return Object.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
//        Message msg = (Message) payload;
//        logger.info("Received : " + msg.getText() + " from : " + msg.getFrom());
        logger.info("Received: " + payload.toString());
    }

    /**
     * A sample message instance.
     * @return instance of <code>Message</code>
     */
    private Message getSampleMessage() {
        Message msg = new Message();
        msg.setFrom("Nicky");
        msg.setText("Howdy!!");
        return msg;
    }



}
