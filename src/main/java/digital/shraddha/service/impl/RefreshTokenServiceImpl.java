package digital.shraddha.service.impl;

import digital.shraddha.mapper.RefreshTokenMapper;
import digital.shraddha.model.dto.RefreshTokenDto;
import digital.shraddha.model.dto.RefreshTokenRequest;
import digital.shraddha.model.entity.RefreshToken;
import digital.shraddha.properties.JwtProperties;
import digital.shraddha.repo.RefreshTokenRepo;
import digital.shraddha.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

	private final RefreshTokenRepo refreshTokenRepo;
	private final RefreshTokenMapper refreshTokenMapper;
	private final JwtProperties jwtProperties;

	@Override
	public RefreshTokenDto createOrRotateRefreshToken(RefreshTokenDto request) {
		var expiryTime = LocalDateTime.now().plusHours(jwtProperties.getRefreshExpiration());

		var refreshToken = switch (request) {
			case RefreshTokenDto (
					var userId, var deviceId, var deviceName, var token, var previousToken, LocalDateTime expiresAt
			)
					when token != null && ! token.isBlank() -> {

				// 🔹 Find by current or previous token
				var rt = refreshTokenRepo
						.findByUserIdAndDeviceIdAndRevokedFalse(userId, deviceId)
						.orElseThrow(() -> new IllegalArgumentException("No refresh token found for device."));

				if (token.equals(rt.getPreviousToken())) {
					revokeAllTokensForUser(userId);
					throw new IllegalStateException("Reused refresh token detected. All tokens revoked.");
				}

				if (! token.equals(rt.getToken())) {
					throw new IllegalArgumentException("Invalid refresh token.");
				}

				if (! rt.isValid()) {
					throw new IllegalArgumentException("Refresh token is expired or revoked.");
				}

				rt.rotateToken(UUID.randomUUID().toString(), expiryTime);
				yield rt;
			}

			// 🔹 No token provided → fallback
			case RefreshTokenDto (
					var userId, var deviceId, var deviceName, var token, var previousToken, LocalDateTime expiresAt
			) -> refreshTokenRepo.findByUserIdAndDeviceId(userId, deviceId)
					.map(rt -> rt.isValid() ? rt : rotate(rt, expiryTime))
					.orElseGet(() -> createNewToken(request, expiryTime));
		};

		return refreshTokenMapper.toDto(refreshTokenRepo.save(refreshToken));
	}

	private RefreshToken rotate(RefreshToken rt, LocalDateTime expiryTime) {
		rt.rotateToken(UUID.randomUUID().toString(), expiryTime);
		return rt;
	}

	private RefreshToken createNewToken(RefreshTokenDto request, LocalDateTime expiryTime) {
		var newRt = refreshTokenMapper.toEntity(request);
		newRt.setToken(UUID.randomUUID().toString());
		newRt.setExpiresAt(expiryTime);
		return newRt;
	}

	@Override
	public RefreshTokenDto getByToken(String token) {
		return refreshTokenRepo.findByToken(token)
				.map(refreshTokenMapper :: toDto)
				.orElse(null);
	}

	@Override
	public boolean validateRefreshToken(String token) {
		return refreshTokenRepo.findByToken(token)
				.map(RefreshToken :: isValid)
				.orElse(false);
	}

	@Override
	public void revokeRefreshToken(String token) {
		refreshTokenRepo.findByToken(token).ifPresent(rt -> {
			rt.setRevoked(true);
			refreshTokenRepo.save(rt);
		});
	}

	@Override
	@Transactional
	public void revokeAllTokensForUser(UUID userId) {
		refreshTokenRepo.findAllByUserId(userId).forEach(rt -> {
			rt.setRevoked(true);
			refreshTokenRepo.save(rt);
		});
	}

	@Override
	public void revokeAllTokensForDevice(UUID userId, UUID deviceId) {
		refreshTokenRepo.findAllByDeviceId(userId).forEach(rt -> {
			rt.setRevoked(true);
			refreshTokenRepo.save(rt);
		});
	}

	@Override
	public RefreshTokenDto getByTokenAndUserIdAndDeviceId(String token, UUID userId, UUID deviceId) {
		return refreshTokenRepo.findByTokenAndUserIdAndDeviceId(token, userId, deviceId)
				.map(refreshTokenMapper :: toDto)
				.orElse(null);
	}

	@Override
	@Transactional
	public void deleteRefreshToken(RefreshTokenRequest request) {
		refreshTokenRepo.deleteByUserIdAndDeviceId(request.userId(), request.deviceId());
	}
}
