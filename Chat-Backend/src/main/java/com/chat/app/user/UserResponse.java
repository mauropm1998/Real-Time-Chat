package com.chat.app.user;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    private String id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private LocalDateTime lastSeen;
    private boolean isOnline;

}
