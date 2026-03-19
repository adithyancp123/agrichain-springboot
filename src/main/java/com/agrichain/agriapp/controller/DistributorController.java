package com.agrichain.agriapp.controller;

import com.agrichain.agriapp.config.ApiResponse;
import com.agrichain.agriapp.model.Distributor;
import com.agrichain.agriapp.service.DistributorService;
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
@RequestMapping("/distributors")
public class DistributorController {

	private final DistributorService distributorService;

	public DistributorController(DistributorService distributorService) {
		this.distributorService = distributorService;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<Distributor>> create(@Valid @RequestBody Distributor distributor) {
		Distributor created = distributorService.create(distributor);
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
	public ResponseEntity<ApiResponse<List<Distributor>>> getAll() {
		List<Distributor> distributors = distributorService.getAll();
		return ResponseEntity.ok(new ApiResponse<>("Success", distributors, HttpStatus.OK.value()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<Distributor>> getById(@PathVariable Long id) {
		Distributor distributor = distributorService.getById(id);
		if (distributor == null) {
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body(new ApiResponse<>("Distributor not found", null, HttpStatus.NOT_FOUND.value()));
		}
		return ResponseEntity.ok(new ApiResponse<>("Success", distributor, HttpStatus.OK.value()));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<Distributor>> update(
			@PathVariable Long id,
			@Valid @RequestBody Distributor distributor
	) {
		Distributor updated = distributorService.update(id, distributor);
		if (updated == null) {
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body(new ApiResponse<>("Distributor not found", null, HttpStatus.NOT_FOUND.value()));
		}
		return ResponseEntity.ok(new ApiResponse<>("Success", updated, HttpStatus.OK.value()));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		boolean deleted = distributorService.delete(id);
		if (!deleted) {
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body(new ApiResponse<>("Distributor not found", null, HttpStatus.NOT_FOUND.value()));
		}
		return ResponseEntity.ok(new ApiResponse<>("Deleted", null, HttpStatus.OK.value()));
	}
}

