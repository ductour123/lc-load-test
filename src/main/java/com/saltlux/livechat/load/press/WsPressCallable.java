package com.saltlux.livechat.load.press;

import com.saltlux.livechat.load.util.SystemUtil;
import com.saltlux.livechat.load.util.WsSessionUtil;
import com.saltlux.livechat.load.vo.ResponseInfo;
import com.saltlux.livechat.load.vo.WsResultShare;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Slf4j
public class WsPressCallable implements Callable<List<ResponseInfo>> {
    private List<String> turnList_1;
    private List<String> turnList_2;
    private WsSessionUtil wsRest;
    private String conversationId;
    private WsResultShare resultShare;

    @Override
    public List<ResponseInfo> call() throws Exception {
        List<ResponseInfo> result = new ArrayList<>();

        // creat conversation
        StopWatch sw = StopWatch.createStarted();
        try {
//            resultShare.increaseOpenConversationTotal();
            conversationId = wsRest.startConversation(resultShare);
            resultShare.increaseOpenConversation(true);
        } catch (Exception e) {
            sw.stop();
            log.error("Failed to start conversation ({}): {}", SystemUtil.toHumanReadableTimeFormat(sw.getTime()), e.getLocalizedMessage());
            resultShare.increaseOpenConversation(false);
        }

        // do conversation turn
        int conversationMsgTotal = 0;
        try {
            if(!StringUtils.isEmpty(conversationId)) {
                // send msg time 1
                for (String message : turnList_1) {
                    // Thời gian delay giữa 2 lần gửi message trong 1 conversation
                    SystemUtil.sleep(TimeUnit.MILLISECONDS, getRandomNumber(3000, 5000));

                    wsRest.sendMessage(message, conversationId);
                    // Tăng số lần request
                    resultShare.addRq(1);
                    resultShare.addBeforeReqAgentRqCnt(1);
                    conversationMsgTotal++;
                }

                // request agent
                wsRest.requestAgent(conversationId);

                // send msg time 2
                for (String message : turnList_2) {
                    // Thời gian delay giữa 2 lần gửi message trong 1 conversation
                    SystemUtil.sleep(TimeUnit.MILLISECONDS, getRandomNumber(3000, 5000));

                    wsRest.sendMessage(message, conversationId);
                    // Tăng số lần request
                    resultShare.addRq(1);
                    conversationMsgTotal++;
                }

            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        // close conversation
        try {
            if(null != conversationId) {
                SystemUtil.sleep(TimeUnit.MILLISECONDS, getRandomNumber(3000, 5000));
                wsRest.closeConversatioId(conversationId, resultShare);
            }
        } catch (Exception e) {
            log.error("Failed to close conversation: {}", e.getLocalizedMessage());
        }

        // add Total msg and time converstion to resultShare
        resultShare.conversationMsgTotalLst.add(conversationMsgTotal);
        resultShare.converationTimeLst.add(sw.getTime());

        return result;
    }

    public WsPressCallable(List<String> turnList_1, List<String> turnList_2, WsSessionUtil wsRest, WsResultShare resultShare) {
        super();
        this.turnList_1 = turnList_1;
        this.turnList_2 = turnList_2;
        this.wsRest = wsRest;
        this.resultShare = resultShare;
    }

    @SuppressWarnings("unused")
    private WsPressCallable() {}

    public int getRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }
}
