package com.saltlux.livechat.load.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SessionWsDTO {
    private String sessionId;
    private String browser;
    private String city;
    private String country;
    private String device;
    private String ipAddress;
    private String livechatId;
    private String message;
    private String os;
    private String source;
}
