package com.agrichain.agriapp.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FarmerRequestDTO {

	@NotBlank
	private String name;

	@NotBlank
	private String region;

	@Min(0)
	private int experienceYears;
}

