package com.agrichain.agriapp.controller;

import com.agrichain.agriapp.config.ApiResponse;
import com.agrichain.agriapp.model.User;
import com.agrichain.agriapp.repository.UserRepository;
import com.agrichain.agriapp.security.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;

	public AuthController(
			UserRepository userRepository,
			PasswordEncoder passwordEncoder,
			JwtService jwtService,
			AuthenticationManager authenticationManager
	) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.authenticationManager = authenticationManager;
	}

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequestDTO request) {
		if (userRepository.findByUsername(request.getUsername()).isPresent()) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(new ApiResponse<>("Username already exists", null, HttpStatus.BAD_REQUEST.value()));
		}

		String roleToSave = (request.getRole() == null || request.getRole().isBlank()) ? "USER" : request.getRole();

		User user = new User();
		user.setUsername(request.getUsername());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole(roleToSave);

		userRepository.save(user);
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(new ApiResponse<>("User registered", user.getUsername(), HttpStatus.CREATED.value()));
	}

	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequestDTO request) {
		logger.info("Login request received for username='{}'", request.getUsername());

		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
			);

			String username = authentication.getName();
			String role = authentication.getAuthorities().stream()
					.map(GrantedAuthority::getAuthority)
					.map(r -> r.startsWith("ROLE_") ? r.substring(5) : r)
					.findFirst()
					.orElse("USER");

			String token = jwtService.generateToken(username, role);
			logger.info("Login successful for username='{}'", username);

			return ResponseEntity.ok(Map.of(
					"success", true,
					"data", token
			));
		} catch (AuthenticationException e) {
			logger.warn("Login failed for username='{}': {}", request.getUsername(), e.getMessage());
			return ResponseEntity
					.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of(
							"success", false,
							"message", "Invalid username or password"
					));
		}
	}

	public static class RegisterRequestDTO {
		@NotBlank
		private String username;

		@NotBlank
		private String password;

		private String role;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getRole() {
			return role;
		}

		public void setRole(String role) {
			this.role = role;
		}
	}

	public static class LoginRequestDTO {
		@NotBlank
		private String username;

		@NotBlank
		private String password;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}
}

