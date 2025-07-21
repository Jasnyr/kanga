package spreadCalc.config;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class StaticTokenAuthFilter extends OncePerRequestFilter {

	private static final String EXPECTED_TOKEN = "ABC123";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.equals("Bearer " + EXPECTED_TOKEN)) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing token");
			return;
		}

		filterChain.doFilter(request, response);
	}
}
