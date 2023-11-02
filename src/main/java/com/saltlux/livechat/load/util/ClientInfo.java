package com.saltlux.livechat.load.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClientInfo {
    private String browser;
    private String city;
    private String country;
    private String device;
    private String ipAddress;
    private String livechatId;
    private String os;
    private String userAgent;
    private String source;
    private long latitude;
    private long longitude;
}
