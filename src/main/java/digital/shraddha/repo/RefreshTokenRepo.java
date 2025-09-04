package digital.shraddha.repo;

import digital.shraddha.model.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, UUID> {

	Optional<RefreshToken> findByToken(String token);

	Optional<RefreshToken> findByUserIdAndDeviceId(UUID userId, UUID deviceId);

	boolean existsByUserIdAndDeviceId(UUID userId, UUID deviceId);

	boolean existsByUserId(UUID userId);

	Optional<RefreshToken> findByUserIdAndDeviceName(UUID userId, String deviceName);

	List<RefreshToken> findAllByUserId(UUID userId);

	Optional<RefreshToken> findByTokenAndUserIdAndDeviceId(String token, UUID userId, UUID deviceId);

	@Modifying
	@Query ("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.userId = :userId AND rt.deviceId = :deviceId")
	void revokeAllByUserIdAndDeviceId(UUID userId, UUID deviceId);

	@Modifying
	@Query ("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.userId = :userId")
	void revokeAllByUserId(UUID userId);

	Optional<RefreshToken> findByUserIdAndDeviceIdAndRevokedFalse(UUID userId, UUID deviceId);

	List<RefreshToken> findAllByDeviceId(UUID userId);

	@Modifying
	void deleteByUserIdAndDeviceId(UUID userId, UUID deviceId);
}
