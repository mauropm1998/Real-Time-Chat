package com.chat.app.user;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.chat.app.chat.Chat;
import com.chat.app.user.tokens.UserJwtToken;
import com.chat.app.utils.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
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
@Table(name = "users")
@NamedQuery(name = UserConstants.FIND_USER_BY_EMAIL, query = "SELECT u FROM User u WHERE u.email = :email")
@NamedQuery(name = UserConstants.FIND_ALL_USERS_EXCEPT_SELF, query = "SELECT u FROM User u WHERE u.id != :userId")
@NamedQuery(name = UserConstants.FIND_USER_BY_ID, query = "SELECT u FROM User u WHERE u.id = :userId")
@NamedQuery(name = UserConstants.FIND_ALL_USERS_EXCEPT_SELF_BY_NAME, query = "SELECT u FROM User u WHERE u.id != :userId AND (u.fullname LIKE CONCAT('%', :name, '%') OR u.email LIKE CONCAT('%', :name, '%') OR u.phone LIKE CONCAT('%', :name, '%'))")
public class User extends BaseEntity implements UserDetails, Principal {

	private static final int LAST_ACTIVE_INTERVAL = 5;

	private String fullname;
	@Column(nullable = false, unique = true)
	private String email;
	@Column(nullable = false, unique = true)
	private String phone;
	@Column(nullable = false)
	private String password;

	private LocalDateTime lastSeen;

	@OneToMany(mappedBy = "sender")
	private List<Chat> chatsAsSender;
	@OneToMany(mappedBy = "recipient")
	private List<Chat> chatsAsRecipient;

	@Transient
	public boolean isUserOnline() {
		return lastSeen != null && lastSeen.isAfter(LocalDateTime.now().minusMinutes(LAST_ACTIVE_INTERVAL));
	}

	@OneToMany(mappedBy = "user")
	private List<UserJwtToken> tokens;

	@Override
	public String getName() {
		return email;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of();
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return password;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return email;
	}

}
