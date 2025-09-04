package digital.shraddha.model.dto;

import java.util.UUID;

public record RefreshTokenRequest(
		UUID userId,
		String deviceName,
		UUID deviceId
) {
}
