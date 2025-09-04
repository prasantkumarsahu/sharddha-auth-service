package digital.shraddha.clients;

import digital.shraddha.model.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@FeignClient (name = "USER-SERVICE", path = "/user-service/api/v1")
public interface UserClient {

	@PostMapping ("/users")
	UserDto createUser(UserDto userDto);

	@GetMapping ("/users/username/{username}")
	UserDto getUserByUsername(@PathVariable String username);

	@GetMapping ("/users/exists/username/{username}")
	boolean userExistsByUsername(@PathVariable String username);

	@GetMapping ("/users/exists/email/{email}")
	boolean userExistsByEmail(@PathVariable String email);

	@DeleteMapping ("/users/{id}")
	void deleteUser(@PathVariable UUID id);
}
