package digital.shraddha.service.impl;

import digital.shraddha.clients.UserClient;
import digital.shraddha.mapper.UserMapper;
import digital.shraddha.model.dto.*;
import digital.shraddha.model.entity.AuthUser;
import digital.shraddha.repo.AuthUserRepo;
import digital.shraddha.security.JwtService;
import digital.shraddha.service.AuthService;
import digital.shraddha.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final UserClient userClient;
	private final UserMapper userMapper;
	private final AuthUserRepo authUserRepo;
	private final PasswordEncoder passwordEncoder;
	private final RefreshTokenService refreshTokenService;
	private final JwtService jwtService;

	@Override
	public LoginResponse login(LoginUserRequest request) {
		AuthUser authUser = authUserRepo.findByUsernameOrEmail(request.emailOrUsername(), request.emailOrUsername())
				.orElseThrow(() -> new IllegalArgumentException("Invalid username/email or password."));

		if (! passwordEncoder.matches(request.password(), authUser.getPassword())) {
			throw new IllegalArgumentException("Invalid username/email or password.");
		}

		return generateTokens(request.emailOrUsername(), authUser, request.deviceName(), request.deviceId());
	}

	@Override
	public LoginResponse refreshToken(RefreshTokenDto request) {
		// Validate refresh token
		RefreshTokenDto validatedToken = refreshTokenService.createOrRotateRefreshToken(request);

		AuthUser authUser = authUserRepo.findById(validatedToken.userId())
				.orElseThrow(() -> new IllegalArgumentException("User not found for the given refresh token."));

		// Issue new access token (short-lived)
		String accessToken = jwtService.generateToken(authUser.getUsername());

		return new LoginResponse(
				accessToken,
				validatedToken.token(),
				validatedToken.expiresAt(),
				authUser.getUserId().toString(),
                validatedToken.deviceId().toString(),
				authUser.getUsername(),
				authUser.getEmail()
		);
	}

	@Override
	public void logout(RefreshTokenRequest request) {
		refreshTokenService.deleteRefreshToken(request);
	}

	@Override
	public String resetPassword(ResetPasswordRequest request) {
		AuthUser authUser = authUserRepo.findById(request.userId())
				.orElseThrow(() -> new IllegalArgumentException("User with id:  " + request.userId() + " not found."));

		if (! passwordEncoder.matches(request.oldPassword(), authUser.getPassword()))
			throw new IllegalArgumentException("Old password is incorrect.");

		if (request.oldPassword().equals(request.newPassword()))
			throw new IllegalArgumentException("New password must be different from the old password.");

		if (! request.newPassword().equals(request.confirmPassword()))
			throw new IllegalArgumentException("New password and confirm new password do not match.");

		authUser.setPassword(passwordEncoder.encode(request.newPassword()));
		authUserRepo.save(authUser);

		return "Password reset successfully.";
	}

	@Override
	public String forgetPassword(ResetPasswordRequest request) {
		AuthUser authUser = authUserRepo.findByUsernameOrEmail(request.usernameOrEmail(), request.usernameOrEmail())
				.orElseThrow(() -> new IllegalArgumentException("User with username/email:  " + request.usernameOrEmail() + " not found."));

		// TODO: Verify by otp to email or phone can be added here

		if (! request.newPassword().equals(request.confirmPassword()))
			throw new IllegalArgumentException("New password and confirm new password do not match.");

		authUser.setPassword(passwordEncoder.encode(request.newPassword()));
		authUserRepo.save(authUser);

		return "Password changed successfully.";
	}

	@Override
	@Transactional
	public void deleteUser(UUID userId) {
		refreshTokenService.revokeAllTokensForUser(userId);
		authUserRepo.findById(userId).ifPresent(authUser -> {
			authUser.setStatus(AuthUser.UserStatus.DELETED);
			authUserRepo.save(authUser);
		});
		userClient.deleteUser(userId);
	}

	@Override
	public LoginResponse register(RegisterUserRequest request) {
		boolean existsByUsername = userClient.userExistsByUsername(request.username());
		boolean existsByEmail = userClient.userExistsByEmail(request.email());
		if (existsByUsername) {
			throw new IllegalArgumentException("User with username " + request.username() + " already exists.");
		}
		if (existsByEmail) {
			throw new IllegalArgumentException("User with email " + request.email() + " already exists.");
		}

		UserDto userDto = userMapper.toDto(request);
		UserDto user = userClient.createUser(userDto);

		AuthUser authUser = userMapper.toAuthUser(user);
		authUser.setPassword(passwordEncoder.encode(request.password()));
		authUser = authUserRepo.save(authUser);

		return generateTokens(authUser.getUsername(), authUser, request.deviceName(), null);
	}

	private LoginResponse generateTokens(String sub, AuthUser authUser, String deviceName, UUID deviceId) {
		String accessToken = jwtService.generateToken(sub);

		UUID finalDeviceId = deviceId != null ? deviceId : UUID.randomUUID();

		String finalDeviceName = deviceName != null ? deviceName : "";

		RefreshTokenDto refreshTokenDto = RefreshTokenDto.forCreation(authUser.getUserId(), finalDeviceId, finalDeviceName);
		RefreshTokenDto storedRefreshToken = refreshTokenService.createOrRotateRefreshToken(refreshTokenDto);

		return new LoginResponse(
				accessToken,
				storedRefreshToken.token(),
				storedRefreshToken.expiresAt(),
				authUser.getUserId().toString(),
                storedRefreshToken.deviceId().toString(),
				authUser.getUsername(),
				authUser.getEmail()
		);
	}
}
