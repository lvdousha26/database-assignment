package com.mingbo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatResponse {
    private boolean success;
    private String message;

    public static ChatResponse ok(String message) {
        return new ChatResponse(true, message);
    }

    public static ChatResponse fail(String message) {
        return new ChatResponse(false, message);
    }
}
