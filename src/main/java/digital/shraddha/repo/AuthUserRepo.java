package digital.shraddha.repo;

import digital.shraddha.model.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthUserRepo extends JpaRepository<AuthUser, UUID> {

	Optional<AuthUser> findByUsername(String username);

	Optional<AuthUser> findByEmail(String email);

	Optional<AuthUser> findByUsernameOrEmail(String username, String email);
}
