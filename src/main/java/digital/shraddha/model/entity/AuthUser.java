package digital.shraddha.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table (
		name = "auth_users",
		indexes = {
				@Index (name = "idx_auth_users_status", columnList = "status"),
				@Index (name = "idx_auth_users_created_at", columnList = "createdAt")
		},
		uniqueConstraints = {
				@UniqueConstraint (name = "uk_auth_users_email", columnNames = "email"),
				@UniqueConstraint (name = "uk_auth_users_username", columnNames = "username")
		}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners (AuditingEntityListener.class)
@SQLRestriction ("status <> 'DELETED'")
public class AuthUser {

	@Id
	private UUID userId;

	@Column (nullable = false, unique = true)
	private String email;

	@Column (nullable = false, unique = true)
	private String username;

	@Column (nullable = false)
	private String password;

	@Column (nullable = false)
	@Builder.Default
	private boolean enabled = true;

	@Enumerated (EnumType.STRING)
	@Builder.Default
	private UserStatus status = UserStatus.ACTIVE;

	@Column (nullable = false)
	@Builder.Default
	private boolean locked = false;

	@CreatedDate
	@Column (nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	private LocalDateTime updatedAt;

	public enum UserStatus {
		ACTIVE, INACTIVE, BANNED, DELETED
	}
}
