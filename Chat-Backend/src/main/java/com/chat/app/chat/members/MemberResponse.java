package com.chat.app.chat.members;

import com.chat.app.user.UserResponse;

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
public class MemberResponse {

    private MemberRole role;
    private String enterDate;
    private UserResponse user;

}
