package com.mingbo.service;

import com.mingbo.pojo.Message;

import java.util.List;
import java.util.Map;

public interface MessageService {
    void sendMessage(Long receiverId, String message);
    void sendSystemMessage(long receiverId, String message);
    long getUncheckedCount();
    List<Map<String, Object>> getConversations();
    List<Message> getConversation(Long contactId, int page, int pageSize);
    void markAsRead(Long senderId);
}
