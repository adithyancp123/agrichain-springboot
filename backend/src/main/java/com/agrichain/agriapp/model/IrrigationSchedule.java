package com.agrichain.agriapp.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IrrigationSchedule {

	@Min(1)
	private Long id;

	@NotBlank
	private String date;

	@NotBlank
	private String method;

	@NotNull
	@Min(1)
	private Long fieldId;
}

