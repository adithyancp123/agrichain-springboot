package com.agrichain.agriapp.controller;

import com.agrichain.agriapp.config.ApiResponse;
import com.agrichain.agriapp.model.Crop;
import com.agrichain.agriapp.service.CropService;
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
@RequestMapping("/crops")
public class CropController {

	private final CropService cropService;

	public CropController(CropService cropService) {
		this.cropService = cropService;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<Crop>> create(@Valid @RequestBody Crop crop) {
		Crop created = cropService.create(crop);
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
	public ResponseEntity<ApiResponse<List<Crop>>> getAll() {
		List<Crop> crops = cropService.getAll();
		return ResponseEntity.ok(new ApiResponse<>("Success", crops, HttpStatus.OK.value()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<Crop>> getById(@PathVariable Long id) {
		Crop crop = cropService.getById(id);
		if (crop == null) {
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body(new ApiResponse<>("Crop not found", null, HttpStatus.NOT_FOUND.value()));
		}
		return ResponseEntity.ok(new ApiResponse<>("Success", crop, HttpStatus.OK.value()));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<Crop>> update(
			@PathVariable Long id,
			@Valid @RequestBody Crop crop
	) {
		Crop updated = cropService.update(id, crop);
		if (updated == null) {
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body(new ApiResponse<>("Crop not found", null, HttpStatus.NOT_FOUND.value()));
		}
		return ResponseEntity.ok(new ApiResponse<>("Success", updated, HttpStatus.OK.value()));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		boolean deleted = cropService.delete(id);
		if (!deleted) {
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body(new ApiResponse<>("Crop not found", null, HttpStatus.NOT_FOUND.value()));
		}
		return ResponseEntity.ok(new ApiResponse<>("Deleted", null, HttpStatus.OK.value()));
	}
}

