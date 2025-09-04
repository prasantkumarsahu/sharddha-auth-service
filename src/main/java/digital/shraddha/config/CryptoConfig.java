package digital.shraddha.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;
import java.util.Base64;

@Configuration
public class CryptoConfig {

	@Bean
	public SecureRandom secureRandom() {
		return new SecureRandom();
	}

	@Bean
	public Base64.Encoder base64UrlEncoder() {
		return Base64.getUrlEncoder().withoutPadding();
	}
}
