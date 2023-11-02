package com.saltlux.livechat.load.util;

import com.saltlux.livechat.load.config.TalkbotEndpoint;
import com.saltlux.livechat.load.vo.ResultShare;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
@Slf4j
@AllArgsConstructor
public class RestTemplateUtil {

	private RestTemplate restTemplate;
	private TalkbotEndpoint talkbotEndpoint;

	
//	@Value(value = "${talkbot.url}")
//	private String url;
//	@Value(value = "${talkbot.id}")
//	private String talkbotId;

//	private String baseUrl					= "http://172.16.100.2:7011/api/v1/";
//	private String baseUrl					= "https://goldengate.biz.vn/chatapi/api/v1/";
//	private String talkbotId				= "cd874c35-3f07-40c0-a637-ec2d01c04360";

//	private String baseUrl					= "https://chat.daugiabiensooto.com.vn:9443/chatapi/api/v1/";
//	private String talkbotId				= "eeab862c-8219-45d1-aa16-b425155a7a39";

//	private String baseUrl					= "https://chatbotvn.saltlux.vn/chatapi/api/v1/";
//	private String talkbotId				= "2afcf1a4-dec0-43ce-b5db-18782032a475";

	
	public String createConversationId(ResultShare resultShare) {
		String convId = null;
		HttpHeaders getHeader = new HttpHeaders();
		List<MediaType> tempHeader = new ArrayList<MediaType>();
		tempHeader.add(MediaType.APPLICATION_JSON);
		getHeader.setAccept(tempHeader);
		HttpEntity<String> getEntity = new HttpEntity<String>(getHeader);

		String url = talkbotEndpoint.getStartConversationUrl();
		ResponseEntity<JSONObject> result = restTemplate.exchange(url, HttpMethod.GET, getEntity, JSONObject.class);

		JSONParser parser = new JSONParser();
		JSONObject jo = null;
		try {
			jo = (JSONObject) parser.parse(result.getBody().toString());
		} catch (ParseException e) {
			// resultShare.addErr(1);
			e.printStackTrace();
		}
		
		if(jo == null) {
			log.error("Create Conversation ID Failed");
		} else {
			convId = jo.get("conversationId").toString();
		}

		return convId;
	}

	public void sendMessage(String message, String conversationId, Consumer<CallResult> onComplete) {
		CallResult callResult = new CallResult(CallStatus.Success, 0, null);
		StopWatch sw = StopWatch.createStarted();
		try {
			String url = talkbotEndpoint.getSendMessageUrl(conversationId);
			restTemplate.postForEntity(url, message.getBytes(StandardCharsets.UTF_8), String.class);
		} catch (Exception ex) {
			callResult.setStatus(CallStatus.Error);
			callResult.setException(ex);
		} finally {
			sw.stop();
			callResult.setTook(sw.getTime());
			onComplete.accept(callResult);
		}
	}
	
	public void closeConversatioId(String conversationId, ResultShare resultshare) {
		log.debug("Close Conversation ID : {}", conversationId);
		HttpHeaders getHeader = new HttpHeaders();
		HttpEntity<String> getEntity = new HttpEntity<String>(getHeader);

		try{
			String url = talkbotEndpoint.getStopConversationUrl(conversationId);
			ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET, getEntity, String.class);

			if(result.getStatusCode() != HttpStatus.OK) {
				log.info("close conversation error: {}", result.getStatusCode());
			}

		} catch(Exception e){
			log.info("close conversation error: {}", e.getClass().getSimpleName() );
		}
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CallResult {
		CallStatus status;
		long took;
		Exception exception;
	}

	public enum CallStatus {
		Error,
		Success
	}
}
