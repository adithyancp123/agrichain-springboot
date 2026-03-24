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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
	private static final String DEV_USERNAME = "admin";
	private static final String DEV_PASSWORD = "admin";

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
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
	public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody LoginRequestDTO request) {
		logger.info("Login request received for username='{}' (passwordLength={})",
				request.getUsername(),
				request.getPassword() != null ? request.getPassword().length() : 0);

		// Simple fixed credentials for local development/demo.
		if (DEV_USERNAME.equals(request.getUsername()) && DEV_PASSWORD.equals(request.getPassword())) {
			String token = jwtService.generateToken(DEV_USERNAME, "ADMIN");
			logger.info("Login successful for default dev user '{}'", DEV_USERNAME);
			return ResponseEntity.ok(new ApiResponse<>("Login successful", token, HttpStatus.OK.value()));
		}

		User user = userRepository.findByUsername(request.getUsername()).orElse(null);
		if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			logger.warn("Login failed for username='{}'", request.getUsername());
			return ResponseEntity
					.status(HttpStatus.UNAUTHORIZED)
					.body(new ApiResponse<>("Invalid username or password", null, HttpStatus.UNAUTHORIZED.value()));
		}

		String token = jwtService.generateToken(user.getUsername(), user.getRole());
		logger.info("Login successful for username='{}'", user.getUsername());
		return ResponseEntity
				.ok(new ApiResponse<>("Login successful", token, HttpStatus.OK.value()));
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

