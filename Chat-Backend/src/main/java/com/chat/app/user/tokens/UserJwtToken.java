package com.chat.app.user.tokens;

import com.chat.app.user.User;
import com.chat.app.utils.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "jwt_tokens")
public class UserJwtToken extends BaseEntity {

    @Column(columnDefinition = "TEXT")
    private String token;
    private Boolean expired;
    private Boolean revoked;
    private String sessionId;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;
}
