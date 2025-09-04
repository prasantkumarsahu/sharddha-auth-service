package digital.shraddha.model.dto;

import java.util.UUID;

public record UserDto(UUID id, String username, String email, String phone, String fullName, String bio,
					  String profileImageUrl, boolean emailVerified, boolean phoneVerified, boolean verifiedBadge,
					  String status) {
}
