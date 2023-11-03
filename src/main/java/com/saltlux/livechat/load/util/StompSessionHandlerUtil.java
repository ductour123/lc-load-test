package com.saltlux.livechat.load.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.util.UUID;

public class StompSessionHandlerUtil extends StompSessionHandlerAdapter {

    private Logger logger = LogManager.getLogger(StompSessionHandlerUtil.class);

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        logger.info("New session established : " + session.getSessionId());
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        logger.error("Got an exception", exception);
    }

//    @Override
//    public Type getPayloadType(StompHeaders headers) {
//        return Object.class;
//    }
//
//    @Override
//    public void handleFrame(StompHeaders headers, Object payload) {
//        logger.info("Received: " + payload.toString());
//    }

}
