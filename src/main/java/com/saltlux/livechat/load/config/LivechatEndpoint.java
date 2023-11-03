package com.saltlux.livechat.load.config;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LivechatEndpoint {
    private final LivechatConfig livechatConfig;

    public String getStartConversationUrl() {
        return livechatConfig.getChatApi() + "chat/"+ livechatConfig.getBotId()+"/startConversation?serverId=" + livechatConfig.getServerId() + "&messengerId=SLX_TEST&channelType=Default";
    }

    public String getSendMessageUrl(String convId) {
        return livechatConfig.getChatApi() + "chat/sync/"+ livechatConfig.getBotId()+"/" + convId + "?serverId=" + livechatConfig.getServerId() + "&messengerId="+ livechatConfig.getBotId();
    }

    public String getStopConversationUrl(String convId) {
        return livechatConfig.getChatApi() + "chat/"+ livechatConfig.getBotId()+"/stopConversation/" + convId + "?serverId=" + livechatConfig.getServerId();
    }

    public String getUrlConnectWs() {
        return livechatConfig.getUrlConnectWs();
    }

}
