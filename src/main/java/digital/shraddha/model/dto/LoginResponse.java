package digital.shraddha.model.dto;

import java.time.LocalDateTime;

public record LoginResponse(
		String accessToken,
		String refreshToken,
		LocalDateTime refreshTokenExpiresAt,
		String userId,
		String username,
		String email
) {
}
