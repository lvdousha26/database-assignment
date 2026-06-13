package com.oilwell.controller;

import com.oilwell.pojo.ChatRequest;
import com.oilwell.pojo.Result;
import com.oilwell.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nvidia")
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
