package digital.shraddha.service;

import digital.shraddha.model.dto.*;

import java.util.UUID;

public interface AuthService {

	LoginResponse register(RegisterUserRequest request);

	LoginResponse login(LoginUserRequest request);

	LoginResponse refreshToken(RefreshTokenDto request);

	void logout(RefreshTokenRequest request);

	String resetPassword(ResetPasswordRequest request);

	String forgetPassword(ResetPasswordRequest request);

	void deleteUser(UUID userId);
}
