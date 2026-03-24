package com.agrichain.agriapp.controller;

import com.agrichain.agriapp.config.ApiResponse;
import com.agrichain.agriapp.model.dto.FarmerRequestDTO;
import com.agrichain.agriapp.model.dto.FarmerResponseDTO;
import com.agrichain.agriapp.model.Farmer;
import com.agrichain.agriapp.service.FarmerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/farmers")
public class FarmerController {

	private final FarmerService farmerService;

	public FarmerController(FarmerService farmerService) {
		this.farmerService = farmerService;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<FarmerResponseDTO>> create(@Valid @RequestBody FarmerRequestDTO request) {
		Farmer created = farmerService.create(toEntity(request));
		if (created == null) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(new ApiResponse<>("Bad Request", null, HttpStatus.BAD_REQUEST.value()));
		}
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(new ApiResponse<>("Created", toResponseDTO(created), HttpStatus.CREATED.value()));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<FarmerResponseDTO>>> getAll() {
		List<FarmerResponseDTO> farmers = farmerService.getAll()
				.stream()
				.map(this::toResponseDTO)
				.collect(Collectors.toList());
		return ResponseEntity.ok(new ApiResponse<>("Success", farmers, HttpStatus.OK.value()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<FarmerResponseDTO>> getById(@PathVariable Long id) {
		Farmer farmer = farmerService.getById(id);
		if (farmer == null) {
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body(new ApiResponse<>("Farmer not found", null, HttpStatus.NOT_FOUND.value()));
		}
		return ResponseEntity.ok(new ApiResponse<>("Success", toResponseDTO(farmer), HttpStatus.OK.value()));
	}

	@GetMapping("/search")
	public ResponseEntity<ApiResponse<List<FarmerResponseDTO>>> searchByRegion(@RequestParam String region) {
		List<FarmerResponseDTO> farmers = farmerService.getByRegion(region)
				.stream()
				.map(this::toResponseDTO)
				.collect(Collectors.toList());
		return ResponseEntity.ok(new ApiResponse<>("Success", farmers, HttpStatus.OK.value()));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<FarmerResponseDTO>> update(
			@PathVariable Long id,
			@Valid @RequestBody FarmerRequestDTO request
	) {
		Farmer updated = farmerService.update(id, toEntity(request));
		if (updated == null) {
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body(new ApiResponse<>("Farmer not found", null, HttpStatus.NOT_FOUND.value()));
		}
		return ResponseEntity.ok(new ApiResponse<>("Success", toResponseDTO(updated), HttpStatus.OK.value()));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		boolean deleted = farmerService.delete(id);
		if (!deleted) {
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body(new ApiResponse<>("Farmer not found", null, HttpStatus.NOT_FOUND.value()));
		}
		return ResponseEntity.ok(new ApiResponse<>("Deleted", null, HttpStatus.OK.value()));
	}

	private Farmer toEntity(FarmerRequestDTO request) {
		if (request == null) {
			return null;
		}
		Farmer farmer = new Farmer();
		farmer.setName(request.getName());
		farmer.setRegion(request.getRegion());
		farmer.setExperienceYears(request.getExperienceYears());
		return farmer;
	}

	private FarmerResponseDTO toResponseDTO(Farmer farmer) {
		if (farmer == null) {
			return null;
		}
		return new FarmerResponseDTO(
				farmer.getId(),
				farmer.getName(),
				farmer.getRegion(),
				farmer.getExperienceYears()
		);
	}
}