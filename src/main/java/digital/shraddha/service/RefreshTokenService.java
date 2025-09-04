package digital.shraddha.service;

import digital.shraddha.model.dto.RefreshTokenDto;
import digital.shraddha.model.dto.RefreshTokenRequest;
import digital.shraddha.model.entity.RefreshToken;

import java.util.UUID;

public interface RefreshTokenService {

	RefreshTokenDto createOrRotateRefreshToken(RefreshTokenDto request);

	RefreshTokenDto getByToken(String token);

	boolean validateRefreshToken(String token);

	void revokeRefreshToken(String token);

	void revokeAllTokensForUser(UUID userId);

	void revokeAllTokensForDevice(UUID userId, UUID deviceId);

	RefreshTokenDto getByTokenAndUserIdAndDeviceId(String token, UUID uuid, UUID uuid1);

	void deleteRefreshToken(RefreshTokenRequest request);
}
