package com.agrichain.agriapp.controller;

import com.agrichain.agriapp.config.ApiResponse;
import com.agrichain.agriapp.model.Field;
import com.agrichain.agriapp.service.FieldService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/fields")
public class FieldController {

	private final FieldService fieldService;

	public FieldController(FieldService fieldService) {
		this.fieldService = fieldService;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<Field>> create(@Valid @RequestBody Field field) {
		Field created = fieldService.create(field);
		if (created == null) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(new ApiResponse<>("Bad Request", null, HttpStatus.BAD_REQUEST.value()));
		}
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(new ApiResponse<>("Created", created, HttpStatus.CREATED.value()));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<Field>>> getAll() {
		List<Field> fields = fieldService.getAll();
		return ResponseEntity.ok(new ApiResponse<>("Success", fields, HttpStatus.OK.value()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<Field>> getById(@PathVariable Long id) {
		Field field = fieldService.getById(id);
		if (field == null) {
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body(new ApiResponse<>("Field not found", null, HttpStatus.NOT_FOUND.value()));
		}
		return ResponseEntity.ok(new ApiResponse<>("Success", field, HttpStatus.OK.value()));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<Field>> update(
			@PathVariable Long id,
			@Valid @RequestBody Field field
	) {
		Field updated = fieldService.update(id, field);
		if (updated == null) {
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body(new ApiResponse<>("Field not found", null, HttpStatus.NOT_FOUND.value()));
		}
		return ResponseEntity.ok(new ApiResponse<>("Success", updated, HttpStatus.OK.value()));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		boolean deleted = fieldService.delete(id);
		if (!deleted) {
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body(new ApiResponse<>("Field not found", null, HttpStatus.NOT_FOUND.value()));
		}
		return ResponseEntity.ok(new ApiResponse<>("Deleted", null, HttpStatus.OK.value()));
	}
}

