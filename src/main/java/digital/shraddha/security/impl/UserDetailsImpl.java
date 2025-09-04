package digital.shraddha.security.impl;

import digital.shraddha.model.entity.AuthUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;

public record UserDetailsImpl(AuthUser authUser) implements UserDetails {

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of();
	}

	@Override
	public String getPassword() {
		return authUser.getPassword();
	}

	@Override
	public String getUsername() {
		return StringUtils.hasText(authUser.getUsername()) ? authUser.getUsername() : authUser.getEmail();
	}

	@Override
	public boolean isAccountNonLocked() {
		return authUser.isEnabled();
	}

	@Override
	public boolean isEnabled() {
		return authUser.isEnabled();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
}
