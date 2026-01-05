package com.chat.app.user.tokens;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserJwtTokenRepository extends JpaRepository<UserJwtToken, String> {
    Optional<UserJwtToken> findByToken(String token);

    @Query("""
            SELECT token
                FROM UserJwtToken token, User user
                WHERE token.user.id = :userId
                AND (token.expired = false AND token.revoked = false)
            """)
    List<UserJwtToken> findAllValidTokenByUser(@Param("userId") String userId);

    @Query("""
            SELECT token
                FROM UserJwtToken token
                WHERE token.user.id = :userId
            """)
    Optional<UserJwtToken> findOneByUserId(@Param("userId") Long userId);

    void deleteTokenByUserId(String userId);
}
