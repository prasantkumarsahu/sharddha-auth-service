package digital.shraddha.model.dto;

public record RegisterUserRequest(
		String username,
		String email,
		String phone,
		String deviceName,
		String password,
		String fullName
) {
}