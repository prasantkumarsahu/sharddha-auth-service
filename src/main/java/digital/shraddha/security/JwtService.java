package digital.shraddha.security;

import digital.shraddha.exception.CredentialsExpiredException;
import digital.shraddha.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtService {

	private final JwtProperties jwtProperties;
	private SecretKey secretKey;

	@PostConstruct
	void init() {
		this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtProperties.getSecret()));
	}

	public String generateToken(String usernameOrEmail) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("identifier", usernameOrEmail);

		return Jwts.builder()
				.claims(claims)
				.subject(usernameOrEmail)
				.issuedAt(new java.util.Date())
				.expiration(new java.util.Date(System.currentTimeMillis() + Duration.ofMinutes(jwtProperties.getExpiration()).toMillis()))
				.signWith(secretKey)
				.compact();
	}

	public boolean validateToken(String token, UserDetails userDetails) {
		try {
			String usernameOrEmail = extractUsernameOrEmail(token);
			if (! usernameOrEmail.equals(userDetails.getUsername()))
				throw new BadCredentialsException("Token username mismatch");

			return true;
		} catch (ExpiredJwtException e) {
			throw new CredentialsExpiredException("JWT token has expired", e);
		} catch (JwtException | IllegalArgumentException e) {
			throw new BadCredentialsException("Invalid JWT token", e);
		}
	}

	public String extractUsernameOrEmail(String token) {
		return getClaims(token).getSubject();
	}

	public Claims getClaims(String token) {
		return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
}
