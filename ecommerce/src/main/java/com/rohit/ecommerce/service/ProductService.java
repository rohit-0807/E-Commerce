package com.rohit.ecommerce.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rohit.ecommerce.dto.ProductRequest;
import com.rohit.ecommerce.dto.ProductResponse;
import com.rohit.ecommerce.exception.ResourceNotFoundException;
import com.rohit.ecommerce.model.Category;
import com.rohit.ecommerce.model.Product;
import com.rohit.ecommerce.repository.CategoryRepository;
import com.rohit.ecommerce.repository.ProductRepository;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
        .stream()
        .map(this::toResponse)
        .toList();
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("product ", id));
        return toResponse(product);
    }

    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
        .orElseThrow(() -> new ResourceNotFoundException("category", request.getCategoryId()));

        System.out.println("Category ID received: " + request.getCategoryId());

        Product product = Product.builder()
        .name(request.getName())
        .description(request.getDescription())
        .price(request.getPrice())
        .stockQuantity(request.getStockQuantity())
        .imgUrl(request.getImgUrl())
        .category(category)
        .activate(true)
        .build();

        return toResponse(productRepository.save(product));
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("product", id));

        Category category = categoryRepository.findById(request.getCategoryId())
        .orElseThrow(() -> new ResourceNotFoundException("category", request.getCategoryId()));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setImgUrl(request.getImgUrl());
        product.setCategory(category);

        return toResponse(productRepository.save(product));
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("product", id));

        productRepository.delete(product);
    }

    private ProductResponse toResponse(Product p) {
        return ProductResponse.builder()
        .id(p.getId())
        .name(p.getName())
        .description(p.getDescription())
        .price(p.getPrice())
        .stockQuantity(p.getStockQuantity())
        .imageUrl(p.getImgUrl())
        .active(p.getActivate())
        .categoryId(p.getCategory().getId())
        .categoryName(p.getCategory().getName())
        .build();
    }
}
