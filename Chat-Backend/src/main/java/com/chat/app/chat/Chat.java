package com.chat.app.chat;

import java.time.LocalDateTime;
import java.util.List;

import com.chat.app.chat.members.Member;
import com.chat.app.message.Message;
import com.chat.app.message.MessageState;
import com.chat.app.message.MessageStatus;
import com.chat.app.message.MessageType;
import com.chat.app.user.User;
import com.chat.app.utils.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NamedQuery(name = ChatConstants.FIND_CHAT_BY_SENDER_ID, query = "SELECT DISTINCT c FROM Chat c WHERE c.sender.id = :senderId OR c.recipient.id = :senderId OR EXISTS (SELECT m FROM c.members m WHERE m.user.id = :senderId) ORDER BY c.createdAt DESC")
@NamedQuery(name = ChatConstants.FIND_CHAT_BY_SENDER_ID_AND_RECEIVER_ID, query = "SELECT DISTINCT c FROM Chat c WHERE (c.sender.id = :senderId AND c.recipient.id = :recipientId) OR (c.sender.id = :recipientId AND c.recipient.id = :senderId)")
@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chat")
public class Chat extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private ChatType type;

    private String name;
    private String description;
    private String groupImagePath;
    private String contentType;
    private String mediaExtension;

    @Enumerated(EnumType.STRING)
    private ImageGroupType imageGroupType;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Member> members;

    @OneToMany(mappedBy = "chat", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt DESC")
    private List<Message> messages;

    @Transient
    public String getImageUrl() {
        return "/public/media/images?image=" + groupImagePath + "&contentType=" + contentType;
    }

    @Transient
    public String getChatName(final String senderId) {
        if (type == ChatType.GROUP) {
            return name;
        }

        if (recipient != null && recipient.getId().equals(senderId)) {
            return sender.getFullname();
        }
        return recipient != null ? recipient.getFullname() : null;
    }

    @Transient
    public long getUnreadMessages(final String senderId) {
        if (type == ChatType.GROUP) {
            return messages.stream()
                    .filter(message -> message.getState() == MessageState.SENT
                            || message.getState() == MessageState.RECEIVED)
                    .filter(message -> !message.getSenderId().equals(senderId))
                    .count();
        }

        return messages.stream()
                // Retorna uma nova lista somente com as mensagens cujo o receptor seja o
                // usuário ativo
                .filter(message -> message.getReceiverId().equals(senderId))
                // Retorna uma nova lista somente com as mensagens que ainda não foram lidas
                // pelo receptor
                .filter(message -> message.getState() == MessageState.SENT
                        || message.getState() == MessageState.RECEIVED)
                // Faz a contagem das mensagens não lidas
                .count();
    }

    @Transient
    public String getLastMessage() {
        if (messages != null && !messages.isEmpty()) {
            if (messages.get(messages.size() - 1).getType() == MessageType.AUDIO) {
                return "00:23";
            }

            // Retornar o conteudo, caso o tipo de mensagem for texto
            return messages.get(messages.size() - 1).getContent();
        }
        return null;
    }

    @Transient
    public String getLastMessageId() {
        if (messages != null && !messages.isEmpty()) {
            return messages.get(messages.size() - 1).getId();
        }
        return null;
    }

    @Transient
    public LocalDateTime getLastMessageTime() {
        if (messages != null && !messages.isEmpty()) {
            return messages.get(messages.size() - 1).getCreatedAt();
        }
        return null;
    }

    @Transient
    public MessageState getLastMessageState() {
        if (messages != null && !messages.isEmpty()) {
            return messages.get(messages.size() - 1).getState();
        }
        return MessageState.SENT;
    }

    @Transient
    public MessageStatus getLastMessageStatus() {
        if (messages != null && !messages.isEmpty()) {
            return messages.get(messages.size() - 1).getStatus();
        }
        return null;
    }

    @Transient
    public MessageType getLastMessageType() {
        if (messages != null && !messages.isEmpty()) {
            return messages.get(messages.size() - 1).getType();
        }
        return null;
    }

    @Transient
    public String getLastMessageSenderId() {
        if (messages != null && !messages.isEmpty()) {
            return messages.get(messages.size() - 1).getSenderId();
        }
        return null;
    }

    @Transient
    public String getLastMessageSenderName() {
        if (messages != null && !messages.isEmpty()) {
            if (type == ChatType.GROUP) {
                return members.stream()
                        .filter((member) -> member.getUser().getId().equals(messages.get(messages.size() - 1).getSenderId()))
                        .findFirst()
                        .map((member) -> member.getUser().getFullname()).get();
            }
        }

        return null;
    }

    @Transient
    public String getLastMessageDeletedById() {
        if (messages != null && !messages.isEmpty()) {
            return messages.get(messages.size() - 1).getDeleteById();
        }
        return null;
    }

    @Transient
    public Boolean getLastMessageNotificationGroup() {
        if (messages != null && !messages.isEmpty()) {
            return messages.get(messages.size() - 1).getIsNotificationGroup();
        }
        return false;
    }
}
