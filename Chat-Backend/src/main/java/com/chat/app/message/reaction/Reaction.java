package com.chat.app.message.reaction;

import com.chat.app.message.Message;
import com.chat.app.user.User;
import com.chat.app.utils.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "message_reactions")
public class Reaction extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private ReactionType type; // LIKE, LOVE, HAHA, WOW, SAD, ANGRY, CUSTOM

    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;
   
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Usuário que reagiu à mensagem

}
