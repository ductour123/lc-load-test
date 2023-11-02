package com.saltlux.livechat.load.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class LivechatUtil {

    private RestTemplate restTemplate;

    public String getLivechatId() {
        HttpHeaders getHeader = new HttpHeaders();
        List<MediaType> tempHeader = new ArrayList<MediaType>();
        tempHeader.add(MediaType.APPLICATION_JSON);
        getHeader.setAccept(tempHeader);
        HttpEntity<String> getEntity = new HttpEntity<String>(getHeader);
//
//        String url =

        return "";
    }

}
