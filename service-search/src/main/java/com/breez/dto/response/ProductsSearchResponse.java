package com.breez.dto.response;

import com.breez.dto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductsSearchResponse {

	private boolean hasMore;
	private List<ProductDto> products;

}
