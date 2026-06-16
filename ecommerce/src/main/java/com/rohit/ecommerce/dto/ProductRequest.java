package com.rohit.ecommerce.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductRequest {
    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotNull(message = "price is required")
    @DecimalMin(value = "0.01", message = "price must be grether then 0")
    private BigDecimal price;

    @NotNull(message = "stock Quantity is required")
    @Min(value = 0, message = "stock can not be in Negative")
    private Integer stockQuantity;
    

    private String imgUrl;

    @NotNull(message = "category id is required")
    private Long categoryId;
}
