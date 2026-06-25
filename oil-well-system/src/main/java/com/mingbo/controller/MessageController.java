package com.mingbo.controller;

import com.mingbo.anno.RateLimit;
import com.mingbo.pojo.Result;
import com.mingbo.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/news")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/send")
    @RateLimit(maxRequests = 30, windowSeconds = 60)
    public Result sendMessage(@RequestBody Map<String, Object> body) {
        try {
            Long receiverId = Long.valueOf(body.get("receiverId").toString());
            String message = (String) body.get("message");
            messageService.sendMessage(receiverId, message);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/unread/count")
    public Result getUnreadCount() {
        try {
            long count = messageService.getUncheckedCount();
            return Result.success(Map.of("count", count));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/conversations")
    public Result getConversations() {
        try {
            return Result.success(messageService.getConversations());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/conversation/{contactId}")
    public Result getConversation(@PathVariable Long contactId,
                                  @RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "50") int pageSize) {
        try {
            return Result.success(messageService.getConversation(contactId, page, pageSize));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/read/{senderId}")
    public Result markAsRead(@PathVariable Long senderId) {
        try {
            messageService.markAsRead(senderId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
