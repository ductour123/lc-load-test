package com.saltlux.livechat.load.vo;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ResponseInfo {
	private long responseTime;
	private long requestTime;
	
	@SuppressWarnings("unused")
	private ResponseInfo() {}

	public ResponseInfo(long responseTime, long requestTime) {
		super();
		this.responseTime = responseTime;
		this.requestTime = requestTime;
	}
}
