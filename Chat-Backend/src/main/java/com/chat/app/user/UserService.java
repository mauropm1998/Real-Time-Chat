package com.chat.app.user;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserResponse> getAllUsersExceptSelf(User activeUser) {
        return userRepository.findAllUsersExceptSelf(activeUser.getId()).stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    public List<UserResponse> getAllUsersExceptSelfByName(User activeUser, String name) {
        return userRepository.findAllUsersExceptSelfByName(activeUser.getId(), name).stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

}
