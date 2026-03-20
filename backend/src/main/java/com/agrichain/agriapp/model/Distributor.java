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
public class Distributor {

	@Min(1)
	private Long id;

	@NotBlank
	private String name;

	@NotBlank
	private String contact;

	@NotBlank
	private String interestedCropType;
}

