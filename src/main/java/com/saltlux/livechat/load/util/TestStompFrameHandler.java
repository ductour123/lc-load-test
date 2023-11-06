package com.saltlux.livechat.load.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;
import java.util.Map;

public class TestStompFrameHandler implements StompFrameHandler {
    private Logger logger = LogManager.getLogger(LCStompFrameHandler.class);

    public TestStompFrameHandler() {
    }

    @Override
    public Type getPayloadType(final StompHeaders stompHeaders) {
        return Map.class;
    }

    @Override
    public void handleFrame(final StompHeaders stompHeaders, final Object obj) {

        Map payload = (Map) obj;
        logger.info("Received: " + payload.toString());


    }
}
