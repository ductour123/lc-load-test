package com.saltlux.livechat.load.util;

import com.saltlux.livechat.load.vo.WsResultShare;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

public class LCStompFrameHandler implements StompFrameHandler {
    private Logger logger = LogManager.getLogger(LCStompFrameHandler.class);
    private WsResultShare resultShare;

    public LCStompFrameHandler(WsResultShare resultShare) {
        this.resultShare = resultShare;
    }

    @Override
    public Type getPayloadType(final StompHeaders stompHeaders) {
        return Map.class;
    }

    @Override
    public void handleFrame(final StompHeaders stompHeaders, final Object obj) {
//        if (Objects.nonNull(payload.get("idle"))) {
//            this.resultShare.increaseOpenConversation(true);
//        }
//        Map payload = (Map) obj;
//        logger.info("Received: " + payload.toString());

        // Tăng số lần nhan msg
        resultShare.addRp(1);

    }

}
