package com.agrichain.agriapp.controller;

import com.agrichain.agriapp.config.ApiResponse;
import com.agrichain.agriapp.model.IrrigationSchedule;
import com.agrichain.agriapp.service.IrrigationScheduleService;
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
@RequestMapping("/irrigations")
public class IrrigationScheduleController {

	private final IrrigationScheduleService irrigationScheduleService;

	public IrrigationScheduleController(IrrigationScheduleService irrigationScheduleService) {
		this.irrigationScheduleService = irrigationScheduleService;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<IrrigationSchedule>> create(@Valid @RequestBody IrrigationSchedule schedule) {
		IrrigationSchedule created = irrigationScheduleService.create(schedule);
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
	public ResponseEntity<ApiResponse<List<IrrigationSchedule>>> getAll() {
		List<IrrigationSchedule> schedules = irrigationScheduleService.getAll();
		return ResponseEntity.ok(new ApiResponse<>("Success", schedules, HttpStatus.OK.value()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<IrrigationSchedule>> getById(@PathVariable Long id) {
		IrrigationSchedule schedule = irrigationScheduleService.getById(id);
		if (schedule == null) {
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body(new ApiResponse<>("IrrigationSchedule not found", null, HttpStatus.NOT_FOUND.value()));
		}
		return ResponseEntity.ok(new ApiResponse<>("Success", schedule, HttpStatus.OK.value()));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<IrrigationSchedule>> update(
			@PathVariable Long id,
			@Valid @RequestBody IrrigationSchedule schedule
	) {
		IrrigationSchedule updated = irrigationScheduleService.update(id, schedule);
		if (updated == null) {
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body(new ApiResponse<>("IrrigationSchedule not found", null, HttpStatus.NOT_FOUND.value()));
		}
		return ResponseEntity.ok(new ApiResponse<>("Success", updated, HttpStatus.OK.value()));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		boolean deleted = irrigationScheduleService.delete(id);
		if (!deleted) {
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body(new ApiResponse<>("IrrigationSchedule not found", null, HttpStatus.NOT_FOUND.value()));
		}
		return ResponseEntity.ok(new ApiResponse<>("Deleted", null, HttpStatus.OK.value()));
	}
}

