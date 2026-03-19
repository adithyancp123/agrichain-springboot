package com.agrichain.agriapp.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex,
			HttpServletRequest request
	) {
		List<FieldValidationError> fieldErrors = ex.getBindingResult().getFieldErrors()
				.stream()
				.map(err -> new FieldValidationError(err.getField(), err.getDefaultMessage()))
				.toList();

		ValidationErrorResponse body = new ValidationErrorResponse(
				Instant.now().toString(),
				HttpStatus.BAD_REQUEST.value(),
				"Bad Request",
				"Validation failed",
				request.getRequestURI(),
				fieldErrors
		);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ValidationErrorResponse> handleConstraintViolation(
			ConstraintViolationException ex,
			HttpServletRequest request
	) {
		List<FieldValidationError> fieldErrors = ex.getConstraintViolations()
				.stream()
				.map(GlobalExceptionHandler::toFieldValidationError)
				.toList();

		ValidationErrorResponse body = new ValidationErrorResponse(
				Instant.now().toString(),
				HttpStatus.BAD_REQUEST.value(),
				"Bad Request",
				"Validation failed",
				request.getRequestURI(),
				fieldErrors
		);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}

	private static FieldValidationError toFieldValidationError(ConstraintViolation<?> violation) {
		return new FieldValidationError(
				violation.getPropertyPath().toString(),
				violation.getMessage()
		);
	}

	public record FieldValidationError(String field, String message) {
	}

	public record ValidationErrorResponse(
			String timestamp,
			int status,
			String error,
			String message,
			String path,
			List<FieldValidationError> fieldErrors
	) {
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<RuntimeErrorResponse> handleRuntimeException(
			RuntimeException ex,
			HttpServletRequest request
	) {
		String message = ex.getMessage() != null ? ex.getMessage() : "Unexpected error";

		// Simple mapping for your service-layer "not found" messages.
		HttpStatus status = message.toLowerCase().contains("not found")
				? HttpStatus.NOT_FOUND
				: HttpStatus.INTERNAL_SERVER_ERROR;

		return ResponseEntity
				.status(status)
				.body(new RuntimeErrorResponse(message, status.value(), Instant.now().toString()));
	}

	public record RuntimeErrorResponse(String message, int status, String timestamp) {
	}
}

