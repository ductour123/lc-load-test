package com.saltlux.livechat.load.thread;

import com.saltlux.livechat.load.util.SystemUtil;
import com.saltlux.livechat.load.vo.WsResultShare;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WsCheckThreadAliveCount implements Runnable{
    private ExecutorService executor = null;
    private WsResultShare resultShare;
    private boolean stop;

    @Override
    public void run() {
        while (!stop) {
            System.out.println("-------------");
            System.out.printf("CCU : %s\n", ((ThreadPoolExecutor) this.executor).getActiveCount());
            resultShare.print();

            SystemUtil.sleep(TimeUnit.SECONDS, 5);
        }
    }

    public WsCheckThreadAliveCount(ExecutorService executor, WsResultShare resultShare) {
        super();
        this.executor = executor;
        this.resultShare = resultShare ;
    }

    public void stop() {
        this.stop = true;
    }

    @SuppressWarnings("unused")
    private WsCheckThreadAliveCount() {}
}
