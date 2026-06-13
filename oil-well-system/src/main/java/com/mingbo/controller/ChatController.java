package com.mingbo.controller;

import com.mingbo.pojo.ApiResponse;
import com.mingbo.pojo.ChatRequest;
import com.mingbo.pojo.ChatResponse;
import com.mingbo.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai/chat")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ApiResponse<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        ChatResponse response = chatService.chat(request);
        if (response.isSuccess()) {
            return ApiResponse.success(response);
        } else {
            return ApiResponse.error(response.getMessage());
        }
    }
}
