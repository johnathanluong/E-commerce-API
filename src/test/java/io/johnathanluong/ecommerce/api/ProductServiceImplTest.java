package io.johnathanluong.ecommerce.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.johnathanluong.ecommerce.api.entity.Product;
import io.johnathanluong.ecommerce.api.service.ProductServiceImpl;

class ProductServiceImplTest {
    private ProductServiceImpl productService;
    private Product product;

    @BeforeEach
    void setUp() {
        productService = new ProductServiceImpl();
        
        product = new Product(
            1L, 
            "Headphones", 
            "Good description.",
            new BigDecimal("99.99"), 
            "Electronics", 
            150, 
            LocalDateTime.now(), 
            "SKU123456", 
            "SoundWave"
        );
    }

    @Test
    void testCreateProduct(){
        Product createdProduct = productService.createProduct(product);

        assertNotNull(createdProduct.getId());
        assertEquals("Headphones", createdProduct.getName());
    }

    @Test
    void testGetProductById_Exists(){
        Product createdProduct = productService.createProduct(product);

        Product retrievedProduct = productService.getProductById(createdProduct.getId());

        assertNotNull(retrievedProduct, "Product should exist");
        assertEquals(createdProduct.getId(), retrievedProduct.getId());
        assertEquals("Headphones", retrievedProduct.getName());
    }    

}
