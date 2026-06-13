package com.mingbo.mapper;

import com.mingbo.pojo.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MessageMapper {

    @Insert("INSERT INTO tb_message (sender_id, receiver_id, message) " +
            "VALUES (#{senderId}, #{receiverId}, #{message})")
    void addMessage(long senderId, long receiverId, String message);

    @Select("SELECT COUNT(*) FROM tb_message WHERE receiver_id=#{receiverId} AND checked=0")
    long getUncheckedMessageCount(long receiverId);

    @Select("SELECT COUNT(*) FROM tb_message WHERE receiver_id=#{receiverId} AND sender_id=#{senderId}")
    int getMessageCountWith(long receiverId, long senderId);

    @Select("SELECT * FROM tb_message WHERE " +
            "(sender_id=#{senderId} AND " +
            "receiver_id=#{receiverId}) OR " +
            "(sender_id=#{receiverId} AND receiver_id=#{senderId}) " +
            "ORDER BY sent_time DESC LIMIT #{offset}, #{limit}")
    List<Message> getMessagesWith(long senderId, long receiverId, int offset, int limit);

    @Select("SELECT DISTINCT user_id FROM tb_authorization WHERE admin_id=#{receiverId} OR user_id=#{receiverId}")
    List<Long> getContactUserIds(long receiverId);

    @Select("SELECT DISTINCT admin_id FROM tb_authorization WHERE admin_id=#{receiverId} OR user_id=#{receiverId}")
    List<Long> getContactAdminIds(long receiverId);
}
