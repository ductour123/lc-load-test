package com.saltlux.livechat.load.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public
class ApiResponse {
    String body;
    long duration;
}