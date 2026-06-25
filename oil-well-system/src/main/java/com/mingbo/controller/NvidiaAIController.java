package com.mingbo.controller;

import com.mingbo.pojo.ChatRequest;
import com.mingbo.pojo.Result;
import com.mingbo.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class NvidiaAIController {

    private final ChatService chatService;

    public NvidiaAIController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    public Result chat(@Valid @RequestBody ChatRequest request) {
        var response = chatService.chat(request);
        if (response.isSuccess()) {
            return Result.success(response.getAnswer());
        } else {
            return Result.error(response.getMessage());
        }
    }
}
