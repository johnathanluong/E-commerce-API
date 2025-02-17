package io.johnathanluong.ecommerce.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import io.johnathanluong.ecommerce.api.entity.Product;

public interface ProductController {
    ResponseEntity<Product> createProduct(Product product);
    ResponseEntity<Product> getProductById(Long id);
    ResponseEntity<List<Product>> getAllProducts();
    ResponseEntity<Product> updateProduct(Long id, Product updatedProduct);
    ResponseEntity<Void> deleteProduct(Long id);
}
