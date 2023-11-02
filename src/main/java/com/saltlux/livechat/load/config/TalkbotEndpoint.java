package com.saltlux.livechat.load.config;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TalkbotEndpoint {
    private final TalkbotConfig talkbotConfig;

    public String getStartConversationUrl() {
        return talkbotConfig.getChatApi() + "chat/"+talkbotConfig.getBotId()+"/startConversation?serverId=" + talkbotConfig.getServerId() + "&messengerId=SLX_TEST&channelType=Default";
    }

    public String getSendMessageUrl(String convId) {
        return talkbotConfig.getChatApi() + "chat/sync/"+talkbotConfig.getBotId()+"/" + convId + "?serverId=" + talkbotConfig.getServerId() + "&messengerId="+talkbotConfig.getBotId();
    }

    public String getStopConversationUrl(String convId) {
        return talkbotConfig.getChatApi() + "chat/"+talkbotConfig.getBotId()+"/stopConversation/" + convId + "?serverId=" + talkbotConfig.getServerId();
    }
}
