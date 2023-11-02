package com.saltlux.livechat.load.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class TalkbotConfig {
    @Value("${talkbot.chatApi}")
    private String chatApi;

    @Value("${talkbot.botId}")
    private String botId;

    @Value("${talkbot.serverId}")
    private int serverId;
}
