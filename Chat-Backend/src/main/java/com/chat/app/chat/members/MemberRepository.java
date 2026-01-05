package com.chat.app.chat.members;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {

    boolean existsByUserEmail(String email);
    boolean existsByUserId(String id);
    Optional<Member> findByUserId(String id);

}
