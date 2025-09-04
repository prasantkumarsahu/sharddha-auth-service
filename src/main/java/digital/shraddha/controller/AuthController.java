package digital.shraddha.controller;

import digital.shraddha.model.dto.*;
import digital.shraddha.service.AuthService;
import digital.shraddha.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping ("/auth")
@RequiredArgsConstructor
@Tag (name = "Authentication", description = "Endpoints for user authentication and account management")
public class AuthController {

	private final AuthService authService;
	private final RefreshTokenService refreshTokenService;

	@Operation (
			summary = "Register a new user",
			description = "Creates a new user account and returns a JWT token with refresh token."
	)
	@ApiResponse (
			responseCode = "201",
			description = "User registered successfully",
			content = @Content (schema = @Schema (implementation = LoginResponse.class))
	)
	@PostMapping ("/register")
	public ResponseEntity<LoginResponse> register(@RequestBody RegisterUserRequest request) {
		LoginResponse loginResponse = authService.register(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(loginResponse);
	}

	@Operation (
			summary = "Login user",
			description = "Authenticates a user with email and password, returns JWT and refresh token."
	)
	@ApiResponse (
			responseCode = "200",
			description = "Login successful",
			content = @Content (schema = @Schema (implementation = LoginResponse.class))
	)
	@PostMapping ("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginUserRequest request) {
		LoginResponse loginResponse = authService.login(request);
		return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
	}

	@Operation (
			summary = "Refresh access token",
			description = "Use refresh token to get a new JWT access token."
	)
	@ApiResponse (
			responseCode = "200",
			description = "Token refreshed successfully",
			content = @Content (schema = @Schema (implementation = LoginResponse.class))
	)
	@PostMapping ("/refresh-token")
	public ResponseEntity<LoginResponse> refreshToken(@RequestBody RefreshTokenDto request) {
		LoginResponse loginResponse = authService.refreshToken(request);
		return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
	}

	@Operation (
			summary = "Logout user",
			description = "Invalidates the refresh token to log the user out."
	)
	@ApiResponse (
			responseCode = "200",
			description = "Logout successful"
	)
	@PostMapping ("/logout")
	public ResponseEntity<String> logout(@RequestBody RefreshTokenRequest request) {
		authService.logout(request);
		return ResponseEntity.status(HttpStatus.OK).body("Logout Successfully!!");
	}

	@Operation (
			summary = "Reset password",
			description = "Resets user password using valid verification or token."
	)
	@ApiResponse (
			responseCode = "200",
			description = "Password reset successful"
	)
	@PostMapping ("/reset-password")
	public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
		return ResponseEntity.status(HttpStatus.OK).body(authService.resetPassword(request));
	}

	@Operation (
			summary = "Forgot password",
			description = "Initiates forgot password process by sending a reset link or OTP to the user."
	)
	@ApiResponse (
			responseCode = "200",
			description = "Password reset instructions sent"
	)
	@PostMapping ("/forget-password")
	public ResponseEntity<String> forgetPassword(@RequestBody ResetPasswordRequest request) {
		return ResponseEntity.status(HttpStatus.OK).body(authService.forgetPassword(request));
	}

	@Operation (
			summary = "Delete user",
			description = "Deletes a user by their unique ID."
	)
	@ApiResponse (
			responseCode = "200",
			description = "User deleted successfully"
	)
	@DeleteMapping ("/delete-user/{userId}")
	public ResponseEntity<String> deleteUser(
			@Parameter (description = "UUID of the user to delete") @PathVariable UUID userId) {
		authService.deleteUser(userId);
		return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
	}
}
