package com.chat.app.user;

import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullname())
                .email(user.getEmail())
                .phoneNumber(user.getPhone())
                .lastSeen(user.getLastSeen())
                .isOnline(user.isUserOnline())
                .build();
    }

}
