package digital.shraddha.model.dto;

import java.util.UUID;

public record ResetPasswordRequest(
		UUID userId,
		String usernameOrEmail,
		String oldPassword,
		String newPassword,
		String confirmPassword
) {
}
