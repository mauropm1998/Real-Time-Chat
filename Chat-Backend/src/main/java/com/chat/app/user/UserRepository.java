package com.chat.app.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, String> {
	Optional<User> findByEmail(String email);

	@Query(name = UserConstants.FIND_ALL_USERS_EXCEPT_SELF)
    List<User> findAllUsersExceptSelf(@Param("userId") String userId);

	@Query(name = UserConstants.FIND_ALL_USERS_EXCEPT_SELF_BY_NAME)
     List<User> findAllUsersExceptSelfByName(@Param("userId") String userId, @Param("name") String name);
}
