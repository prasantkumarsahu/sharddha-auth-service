package digital.shraddha.security.impl;

import digital.shraddha.model.entity.AuthUser;
import digital.shraddha.repo.AuthUserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	private final AuthUserRepo authUserRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		String login = username.trim().toLowerCase();
		AuthUser user = authUserRepo.findByUsernameOrEmail(login, login)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + username));

		return new UserDetailsImpl(user);
	}
}
