package digital.shraddha.model.dto;

import java.util.UUID;

public record LoginUserRequest(
		String emailOrUsername,
		String password,
		UUID deviceId,
		String deviceName
) {
}
