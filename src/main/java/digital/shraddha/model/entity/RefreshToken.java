package digital.shraddha.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table (
		name = "refresh_tokens",
		indexes = {
				@Index (name = "idx_refresh_token_user", columnList = "userId"),
				@Index (name = "idx_refresh_token_token", columnList = "token"),
				@Index (name = "idx_refresh_token_device", columnList = "deviceId")
		}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners (AuditingEntityListener.class)
public class RefreshToken {

	@Id
	@GeneratedValue (strategy = GenerationType.UUID)
	private UUID id;

	@Column (nullable = false)
	private UUID userId;

	@Column (nullable = false)
	private UUID deviceId;

	private String deviceName;

	@Column (nullable = false, length = 500)
	private String token;

	@Column (length = 500)
	private String previousToken;

	@Column (nullable = false)
	private LocalDateTime expiresAt;

	@Builder.Default
	private boolean revoked = false;

	@CreatedDate
	@Column (nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	private LocalDateTime updatedAt;

	public void rotateToken(String newToken, LocalDateTime newExpiry) {
		this.previousToken = this.token;
		this.token = newToken;
		this.expiresAt = newExpiry;
	}

	public boolean isValid() {
		return ! revoked && expiresAt.isAfter(LocalDateTime.now());
	}
}
