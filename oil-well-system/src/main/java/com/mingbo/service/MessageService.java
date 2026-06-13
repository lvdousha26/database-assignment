package com.mingbo.service;

import com.mingbo.pojo.Message;
import com.mingbo.pojo.PageVO;

import java.util.List;

public interface MessageService {
    void sendAdminMessage(long receiverId, String message);
    void sendSystemMessage(long receiverId, String message);
    long getUncheckedMessageCount();
    PageVO<Message> getMessagesWith(long senderId, int pageSize, int currentPage);
    List<Long> getSenderIds();
}
