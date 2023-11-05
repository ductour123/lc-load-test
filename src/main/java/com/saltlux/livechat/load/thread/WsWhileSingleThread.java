package com.saltlux.livechat.load.thread;

import com.saltlux.livechat.load.press.WsPressCallable;
import com.saltlux.livechat.load.util.SystemUtil;
import com.saltlux.livechat.load.util.WsSessionUtil;
import com.saltlux.livechat.load.vo.ResponseInfo;
import com.saltlux.livechat.load.vo.WsResultShare;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class WsWhileSingleThread implements Runnable{
    private WsResultShare resultShare;
    private ExecutorService executor;
    private List<Future<List<ResponseInfo>>> futureList;
    private WsSessionUtil wsRest;
    private List<List<String>> conversationList;
    private int ccu;
    private AtomicInteger openedConvCount = new AtomicInteger(0);

    private boolean stop;

    public WsWhileSingleThread(ExecutorService executor, List<Future<List<ResponseInfo>>> futureList,
                               WsSessionUtil wsRest, List<List<String>> dataSet, int ccu, WsResultShare resultShare) {
        super();
        this.executor = executor;
        this.futureList = futureList;
        this.wsRest = wsRest;
        this.conversationList = dataSet;
        this.ccu = ccu;
        this.resultShare = resultShare;

    }

    @Override
    public void run() {
        try {
            while (!stop) {
                if(((ThreadPoolExecutor)this.executor).getActiveCount() < ccu)
                    startNewConversation();

                // SystemUtil.sleep(TimeUnit.MILLISECONDS, SystemUtil.getRandomNumber(200, 500));
                SystemUtil.sleep(TimeUnit.MILLISECONDS, 200);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            resultShare.print();
        }
    }

    private void startNewConversation() {
        List<String> turnList_1 = takeConversationDataToRun();
        List<String> turnList_2 = takeConversationDataToRun();
        int numTurn = SystemUtil.getRandomNumber(5, 10);
        executor.submit(new WsPressCallable(turnList_1, turnList_2.subList(0, numTurn), wsRest, resultShare));

//		log.info("Start conversation {}", openedConvCount.incrementAndGet());
    }

    private List<String> takeConversationDataToRun() {
        int idx = SystemUtil.getRandomNumber(0, this.conversationList.size() - 1);
        return this.conversationList.get(idx);
    }

    public void stop() {
        this.stop = true;
    }

    @SuppressWarnings("unused")
    private WsWhileSingleThread() {}
}
