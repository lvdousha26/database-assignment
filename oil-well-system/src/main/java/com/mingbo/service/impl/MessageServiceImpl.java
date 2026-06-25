package com.mingbo.service.impl;

import com.mingbo.mapper.MessageMapper;
import com.mingbo.mapper.UserMapper;
import com.mingbo.pojo.Message;
import com.mingbo.pojo.User;
import com.mingbo.service.InfoService;
import com.mingbo.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private InfoService infoService;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void sendSystemMessage(long receiverId, String message) {
        Message msg = new Message();
        msg.setSenderId(1L);
        msg.setReceiverId(receiverId);
        msg.setMessage(message);
        messageMapper.insert(msg);
    }

    @Override
    public void sendMessage(Long receiverId, String message) {
        Long senderId = infoService.getOperateUser();
        Message msg = new Message();
        msg.setSenderId(senderId);
        msg.setReceiverId(receiverId);
        msg.setMessage(message);
        messageMapper.insert(msg);
    }

    @Override
    public long getUncheckedCount() {
        return messageMapper.countUnchecked(infoService.getOperateUser());
    }

    @Override
    public List<Map<String, Object>> getConversations() {
        Long userId = infoService.getOperateUser();
        List<Long> contactIds = messageMapper.getContactIds(userId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Long contactId : contactIds) {
            User user = userMapper.selectById(contactId);
            if (user == null) continue;

            List<Message> lastMsgs = messageMapper.getConversation(userId, contactId, 0, 1);
            long unread = messageMapper.countUnchecked(contactId);

            Map<String, Object> conv = new LinkedHashMap<>();
            conv.put("contactId", contactId);
            conv.put("contactName", user.getUsername());
            conv.put("contactRole", user.getRole());
            conv.put("lastMessage", lastMsgs.isEmpty() ? null : lastMsgs.get(0).getMessage());
            conv.put("lastTime", lastMsgs.isEmpty() ? null : lastMsgs.get(0).getSentTime());
            conv.put("unread", unread);
            result.add(conv);
        }
        return result;
    }

    @Override
    public List<Message> getConversation(Long contactId, int page, int pageSize) {
        Long userId = infoService.getOperateUser();
        int offset = (page - 1) * pageSize;
        return messageMapper.getConversation(userId, contactId, offset, pageSize);
    }

    @Override
    public void markAsRead(Long senderId) {
        Long receiverId = infoService.getOperateUser();
        messageMapper.markAsRead(senderId, receiverId);
    }
}
