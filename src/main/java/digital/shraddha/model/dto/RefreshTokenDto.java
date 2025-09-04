package digital.shraddha.model.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RefreshTokenDto(
		UUID userId,
		UUID deviceId,
		String deviceName,
		String token,
		String previousToken,
		LocalDateTime expiresAt
) {
	public static RefreshTokenDto forCreation(UUID userId, UUID deviceId, String deviceName) {
		return new RefreshTokenDto(userId, deviceId, deviceName, null, null, null);
	}
}
