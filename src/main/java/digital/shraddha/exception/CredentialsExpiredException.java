package digital.shraddha.exception;

import org.springframework.security.core.AuthenticationException;

public class CredentialsExpiredException extends AuthenticationException {
	public CredentialsExpiredException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
}
