package com.breez.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SingleProductSearchRequest {

	@NotNull(message = "Id can't be null")
	@Min(value = 1, message = "Id can't be negative or zero")
	private Long id;

}
