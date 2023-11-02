package com.saltlux.livechat.load.thread;

import com.saltlux.livechat.load.press.PressCallable;
import com.saltlux.livechat.load.util.RestTemplateUtil;
import com.saltlux.livechat.load.util.SystemUtil;
import com.saltlux.livechat.load.vo.ResponseInfo;
import com.saltlux.livechat.load.vo.ResultShare;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class WhileSingleThread implements Runnable{

	private ResultShare resultShare;
	private ExecutorService executor;
	private List<Future<List<ResponseInfo>>> futureList;
	private RestTemplateUtil rest;
	private List<List<String>> conversationList;
	private int ccu;
	private AtomicInteger openedConvCount = new AtomicInteger(0);

	private boolean stop;

	public WhileSingleThread(ExecutorService executor, List<Future<List<ResponseInfo>>> futureList,
							 RestTemplateUtil rest, List<List<String>> dataSet, int ccu, ResultShare resultShare) {
		super();
		this.executor = executor;
		this.futureList = futureList;
		this.rest = rest;
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
		List<String> turnList = takeConversationDataToRun();
		executor.submit(new PressCallable(turnList, rest, resultShare));

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
	private WhileSingleThread() {}

}
