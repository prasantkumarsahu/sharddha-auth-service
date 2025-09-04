package digital.shraddha.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties (prefix = "jwt", ignoreUnknownFields = false)
public class JwtProperties {

	private String secret;
	private long expiration;
	private long refreshExpiration;
}
