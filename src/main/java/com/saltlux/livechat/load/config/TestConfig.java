package com.saltlux.livechat.load.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class TestConfig {
    @Value("${tc.ccu.max}")
    private int maxCCU;

    @Value("${tc.duration}")
    private int duration;

    @Value("${tc.ccu.increase}")
    private int ccuIncrease;

    @Value("${tc.ccu.waitTime}")
    private int waitTime;

    @Value("${tc.dataFile}")
    private String dataFile;

    @Value("${lc.livechatId}")
    private String livechatId;

    @Value("${lc.conf.info.orgCode}")
    private String confInfoOrgCode;

    @Value("${lc.conf.info.livechatCode}")
    private String confInfoLivechatCode;

}
