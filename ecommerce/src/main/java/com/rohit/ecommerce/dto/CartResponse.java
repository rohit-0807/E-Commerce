package com.rohit.ecommerce.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class CartResponse {
    
    private Long cartId;
    private List<CartItemResponse> items;
    private BigDecimal total;
}
