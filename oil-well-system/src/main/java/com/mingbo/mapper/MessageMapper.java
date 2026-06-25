package com.mingbo.mapper;

import com.mingbo.pojo.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageMapper {

    @Insert("INSERT INTO tb_message (sender_id, receiver_id, message) VALUES (#{senderId}, #{receiverId}, #{message})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Message message);

    @Select("SELECT COUNT(*) FROM tb_message WHERE receiver_id = #{userId} AND checked = 0")
    long countUnchecked(Long userId);

    @Select("SELECT COUNT(*) FROM tb_message WHERE receiver_id = #{userId} AND sender_id = #{senderId} AND checked = 0")
    long countUncheckedFrom(Long userId, Long senderId);

    @Update("UPDATE tb_message SET checked = 1 WHERE sender_id = #{senderId} AND receiver_id = #{receiverId} AND checked = 0")
    void markAsRead(Long senderId, Long receiverId);

    @Select("SELECT m.* FROM tb_message m WHERE " +
            "(m.sender_id = #{userId} AND m.receiver_id = #{contactId}) OR " +
            "(m.sender_id = #{contactId} AND m.receiver_id = #{userId}) " +
            "ORDER BY m.sent_time DESC LIMIT #{offset}, #{limit}")
    List<Message> getConversation(Long userId, Long contactId, int offset, int limit);

    @Select("SELECT COUNT(*) FROM tb_message WHERE " +
            "(sender_id = #{userId} AND receiver_id = #{contactId}) OR " +
            "(sender_id = #{contactId} AND receiver_id = #{userId})")
    int countConversation(Long userId, Long contactId);

    @Select("SELECT DISTINCT " +
            "CASE WHEN sender_id = #{userId} THEN receiver_id ELSE sender_id END AS contact_id " +
            "FROM tb_message WHERE sender_id = #{userId} OR receiver_id = #{userId} " +
            "ORDER BY contact_id")
    List<Long> getContactIds(Long userId);

    @Select("SELECT m.* FROM tb_message m WHERE " +
            "m.id IN (SELECT MAX(m2.id) FROM tb_message m2 WHERE " +
            "(m2.sender_id = #{userId} OR m2.receiver_id = #{userId}) " +
            "GROUP BY CASE WHEN m2.sender_id = #{userId} THEN m2.receiver_id ELSE m2.sender_id END) " +
            "ORDER BY m.sent_time DESC")
    List<Message> getLatestMessages(Long userId);
}
