package io.johnathanluong.ecommerce.api.service;

import java.util.List;

import io.johnathanluong.ecommerce.api.entity.Product;

public interface ProductService {
    Product createProduct(Product product);
    Product getProductById(Long id);
    List<Product> getAllProducts();
    Product updateProduct(Long id, Product updatedProduct);
    void deleteProduct(Long id);
}
