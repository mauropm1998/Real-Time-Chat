package com.chat.app.message;

import java.util.List;

import com.chat.app.chat.Chat;
import com.chat.app.message.reaction.Reaction;
import com.chat.app.utils.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "messages")
@NamedQuery(name = MessageConstants.FIND_MESSAGES_BY_CHAT_ID, query = "SELECT m FROM Message m WHERE m.chat.id = :chatId ORDER BY m.createdAt")
@NamedQuery(name = MessageConstants.SET_MESSAGES_TO_SEE_BY_CHAT, query = "UPDATE Message SET state = :newState WHERE chat.id = :chatId")
public class Message extends BaseEntity {

    @Column(columnDefinition = "TEXT")
    private String content; // Conteudo da mensagem

    @Enumerated(EnumType.STRING)
    private MessageState state; // SENT, RECEIVED, SEEN
    @Enumerated(EnumType.STRING)
    private MessageStatus status; // DELETED_FOR_ALL, DELETED_FOR_ME, EDITED, FORWARDED
    @Enumerated(EnumType.STRING)
    private MessageType type; // AUDIO, TEXT, VIDEO, IMAGE, POLL, DOCUMENT

    private Boolean isNotificationGroup;

    @ManyToOne
    @JoinColumn(name = "message_replied_id")
    private Message messageReplied; // Mensagem que está sendo respondida, se houver

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reaction> reactions; // Lista de reações à mensagem

    @Column(nullable = false)
    private String senderId;
    private String receiverId;
    private String deleteById;

    @Column(columnDefinition = "TEXT")
    private String mediaFilePath;
    private String contentType; // Tipo do arquivo de mídia (ex: image/png, video/mp4)
    private String mediaExtension;
    @Column(columnDefinition = "TEXT")
    private String mediaName; // Nome do arquivo de mídia (ex: video.mp4, image.png)
    private Long mediaSize;

    public String getMediaUrl( ) {
        if(this.type == MessageType.VIDEO) {
            return "/public/media/videos?video=" + mediaFilePath + "&contentType=" + contentType;
        }
        if(this.type == MessageType.IMAGE) {
            return "/public/media/images?image=" + mediaFilePath + "&contentType=" + contentType;
        }
        if(this.type == MessageType.DOCUMENT) {
            return "/public/media/files?file=" + mediaFilePath + "&contentType=" + contentType;
        }

        return "";
    }

}
