package com.chat.app.utils;

import java.util.Arrays;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.chat.app.user.User;
import com.chat.app.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DatabaseInitializer {

    private final UserRepository userRepository;

    public void createRolesAndUser(PasswordEncoder passwordEncoder) {

        if (userRepository.findAll().isEmpty()) {
            userRepository.save(User.builder().fullname("Mauro Mateus").phone("936816520")
                    .email("mauro.mateus@selectservices.ao").password(passwordEncoder.encode("12345678"))
                    .build());
            userRepository.save(User.builder().fullname("Cristina Francisco").phone("900000000")
                    .email("cristina@chat.ao").password(passwordEncoder.encode("12345678"))
                    .build());
            userRepository.save(User.builder().fullname("Paulo Matias").phone("999999999")
                    .email("paulo@chat.ao").password(passwordEncoder.encode("12345678"))
                    .build());
            userRepository.save(User.builder().fullname("Domingos João").phone("9999999")
                    .email("domingos@chat.ao").password(passwordEncoder.encode("12345678"))
                    .build());
            userRepository.save(User.builder().fullname("Teresa Mateus").phone("99999990439")
                    .email("teresa@chat.ao").password(passwordEncoder.encode("12345678"))
                    .build());
            userRepository.save(User.builder().fullname("Estevão Domingos").phone("9999999043")
                    .email("estevao@chat.ao").password(passwordEncoder.encode("12345678"))
                    .build());
            userRepository.save(User.builder().fullname("Márcia Leitão").phone("999999904334")
                    .email("marcia@chat.ao").password(passwordEncoder.encode("12345678"))
                    .build());
            userRepository.save(User.builder().fullname("Manuel André").phone("99999")
                    .email("manuel@chat.ao").password(passwordEncoder.encode("12345678"))
                    .build());
        }
    }
}
