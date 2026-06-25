package com.mingbo.service;

import com.mingbo.pojo.ChatRequest;

public interface ChatService {
    ChatResponse chat(ChatRequest request);

    class ChatResponse {
        private boolean success;
        private String answer;
        private String message;

        public ChatResponse(boolean success, String answer, String message) {
            this.success = success;
            this.answer = answer;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public String getAnswer() { return answer; }
        public String getMessage() { return message; }

        public static ChatResponse ok(String answer) {
            return new ChatResponse(true, answer, null);
        }

        public static ChatResponse fail(String message) {
            return new ChatResponse(false, null, message);
        }
    }
}
