package com.chat.app.user;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "API para os usuários")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsersExceptSelf(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getAllUsersExceptSelf(user));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> getAllUsersExceptSelfByName(
        @RequestParam("name") String name,
        @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getAllUsersExceptSelfByName(user, name));
    }

}
