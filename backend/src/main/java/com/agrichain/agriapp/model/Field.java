package com.agrichain.agriapp.model;

import jakarta.validation.constraints.DecimalMin;
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
public class Field {

	@Min(1)
	private Long id;

	@NotBlank
	private String name;

	@DecimalMin(value = "0.0", inclusive = true)
	private double area;

	@NotNull
	@Min(1)
	private Long farmerId;
}

