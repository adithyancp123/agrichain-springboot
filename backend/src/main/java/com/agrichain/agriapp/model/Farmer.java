package com.agrichain.agriapp.model;

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
public class Farmer {

	@Min(1)
	private Long id;

	@NotBlank
	private String name;

	@NotBlank
	private String region;

	@Min(0)
	private int experienceYears;
}

