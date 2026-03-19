package com.agrichain.agriapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
		return new InMemoryUserDetailsManager(
				User.withUsername("admin")
						.password(passwordEncoder.encode("admin123"))
						.roles("ADMIN")
						.build()
		);
	}

	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint(ObjectMapper objectMapper) {
		return new AuthenticationEntryPoint() {
			@Override
			public void commence(
					HttpServletRequest request,
					HttpServletResponse response,
					AuthenticationException authException
			) throws IOException {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				// Helps clients/browsers know this is HTTP Basic auth.
				response.setHeader("WWW-Authenticate", "Basic realm=\"AgriChain\"");

				Map<String, Object> body = Map.of(
						"message", "Unauthorized: valid Basic authentication is required",
						"status", HttpServletResponse.SC_UNAUTHORIZED,
						"timestamp", Instant.now().toString()
				);

				objectMapper.writeValue(response.getWriter(), body);
			}
		};
	}

	@Bean
	public SecurityFilterChain securityFilterChain(
			HttpSecurity http,
			AuthenticationEntryPoint authenticationEntryPoint
	) throws Exception {
		// Configure exception handling and Basic auth challenge with a custom response body.
		http
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
				.exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint))
				.httpBasic(b -> b.authenticationEntryPoint(authenticationEntryPoint));

		return http.build();
	}
}

