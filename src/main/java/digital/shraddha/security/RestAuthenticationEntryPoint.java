package digital.shraddha.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import digital.shraddha.model.dto.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

		ErrorResponse errorResponse = new ErrorResponse(
				LocalDateTime.now(),
				HttpServletResponse.SC_UNAUTHORIZED,
				"UNAUTHORIZED",
				"Authentication is required to access this resource.",
				request.getRequestURI(),
				UUID.randomUUID().toString()
		);

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}
}
