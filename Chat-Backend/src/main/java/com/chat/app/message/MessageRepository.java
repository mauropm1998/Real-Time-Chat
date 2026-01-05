package com.chat.app.message;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, String> {

    @Query(name = MessageConstants.FIND_MESSAGES_BY_CHAT_ID)
    List<Message> findMessagesByChatId(@Param("chatId") String chatId);

    List<Message> findByChatIdOrderByCreatedAtAsc(@Param("chatId") String chatId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Message m SET m.state = :state WHERE m.id = :id AND m.chat.id = :chatId")
    int updateMessageState(@Param("id") String id, @Param("chatId") String chatId, @Param("state") MessageState state);

}
