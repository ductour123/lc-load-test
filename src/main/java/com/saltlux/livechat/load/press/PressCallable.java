package com.saltlux.livechat.load.press;

import com.saltlux.livechat.load.util.RestTemplateUtil;
import com.saltlux.livechat.load.util.SystemUtil;
import com.saltlux.livechat.load.vo.ResponseInfo;
import com.saltlux.livechat.load.vo.ResultShare;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PressCallable implements Callable<List<ResponseInfo>>{
	
	private List<String> turnList;
	private RestTemplateUtil rest;
	private String conversationId;
	private ResultShare resultShare;

	@Override
	public List<ResponseInfo> call() throws Exception {
		List<ResponseInfo> result = new ArrayList<>();

		// creat conversation
		StopWatch sw = StopWatch.createStarted();
		try {
			conversationId = rest.createConversationId(resultShare);
			resultShare.increaseOpenConversation(true);
		} catch (Exception e) {
			sw.stop();
			log.error("Failed to start conversation ({}): {}", SystemUtil.toHumanReadableTimeFormat(sw.getTime()), e.getLocalizedMessage());
			resultShare.increaseOpenConversation(false);
		}

		// do conversation turn
		try {
			if(!StringUtils.isEmpty(conversationId)) {
				for (String message : turnList) {
					// Thời gian delay giữa 2 lần gửi message trong 1 conversation
					SystemUtil.sleep(TimeUnit.MILLISECONDS, getRandomNumber(2000, 4000));

					// Tăng số lần request
					resultShare.addRq(1);
					rest.sendMessage(message, conversationId, e -> {
						resultShare.addCallApiResult(e);

						if(e.getException() != null) {
							log.error("Failed to send message ({}): {}", SystemUtil.toHumanReadableTimeFormat(e.getTook()), e.getException().getLocalizedMessage());
						}
					});
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		// close conversation
		try {
			if(null != conversationId)
				rest.closeConversatioId(conversationId, resultShare);
		} catch (Exception e) {
			log.error("Failed to close conversation: {}", e.getLocalizedMessage());
		}
		
		return result;
	}
	
	public PressCallable(List<String> turnList, RestTemplateUtil rest, ResultShare resultShare) {
		super();
		this.turnList = turnList;
		this.rest = rest;
		this.resultShare = resultShare;
	}
	
	@SuppressWarnings("unused")
	private PressCallable() {}

	public int getRandomNumber(int min, int max) {
		Random random = new Random();
		return random.nextInt(max - min) + min;
	}

}
