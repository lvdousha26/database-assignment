package com.mingbo.controller;

import com.mingbo.anno.RateLimit;
import com.mingbo.pojo.GeneralRequestDTO;
import com.mingbo.pojo.Message;
import com.mingbo.pojo.PageVO;
import com.mingbo.pojo.Result;
import com.mingbo.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/news")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping
    @RateLimit(maxRequests = 20, windowSeconds = 60)
    public Result sendMessage(@RequestBody Message message) {
        try {
            messageService.sendAdminMessage(message.getReceiverId(), message.getMessage());
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping
    public Result getMessages(GeneralRequestDTO generalRequestDTO) {
        try {
            long senderId = generalRequestDTO.getUserId();
            int pageSize = generalRequestDTO.getPageSize();
            int currentPage = generalRequestDTO.getCurrentPage();
            PageVO<Message> messagesWith = messageService.getMessagesWith(senderId, pageSize, currentPage);
            return Result.success(messagesWith);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/senders")
    public Result getSenders() {
        try {
            return Result.success(messageService.getSenderIds());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
