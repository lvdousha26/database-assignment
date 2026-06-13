package com.mingbo.service.impl;

import com.mingbo.mapper.MessageMapper;
import com.mingbo.pojo.Message;
import com.mingbo.pojo.PageVO;
import com.mingbo.service.InfoService;
import com.mingbo.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private InfoService infoService;

    @Override
    public void sendAdminMessage(long receiverId, String message) {
        long operateUser = infoService.getOperateUser();
        sendMessage(operateUser, receiverId, message);
    }

    @Override
    public void sendSystemMessage(long receiverId, String message) {
        sendMessage(1, receiverId, message);
    }

    @Override
    public long getUncheckedMessageCount() {
        return messageMapper.getUncheckedMessageCount(infoService.getOperateUser());
    }

    @Override
    public PageVO<Message> getMessagesWith(long senderId, int pageSize, int currentPage) {
        PageVO<Message> pageVO = new PageVO<>();
        pageVO.setTotalCount(messageMapper.getUncheckedMessageCount(infoService.getOperateUser()));
        int offset = (currentPage - 1) * pageSize;
        List<Message> messages = messageMapper.getMessagesWith(senderId, infoService.getOperateUser(), offset, pageSize);
        pageVO.setRows(messages);
        return pageVO;
    }

    @Override
    public List<Long> getSenderIds() {
        List<Long> senderIds = new ArrayList<>();
        senderIds.addAll(messageMapper.getContactAdminIds(infoService.getOperateUser()));
        senderIds.addAll(messageMapper.getContactUserIds(infoService.getOperateUser()));
        senderIds.add(1L);
        return senderIds.stream().distinct().toList();
    }

    private void sendMessage(long senderId, long receiverId, String message) {
        messageMapper.addMessage(senderId, receiverId, message);
    }
}
