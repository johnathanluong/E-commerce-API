package io.johnathanluong.ecommerce.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.johnathanluong.ecommerce.api.entity.Product;
import io.johnathanluong.ecommerce.api.service.ProductServiceImpl;

class ProductServiceImplTest {
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        productService = new ProductServiceImpl();
    }

    @Test
    void testCreateProduct(){
        Product product = new Product(
            null, 
            "Headphones", 
            "Good description.",
            new BigDecimal("99.99"), 
            "Electronics", 
            150, 
            LocalDateTime.now(), 
            "SKU123456", 
            "SoundWave"
        );
        Product createdProduct = productService.createProduct(product);

        assertNotNull(createdProduct.getId());
        assertEquals("Headphones", createdProduct.getName());
        assertEquals("Good description.", createdProduct.getDescription()); 
        assertEquals(new BigDecimal("99.99"), createdProduct.getPrice()); 
        assertEquals("Electronics", createdProduct.getCategory()); 
        assertEquals(150, createdProduct.getStock()); 
        assertEquals("SKU123456", createdProduct.getSku()); 
        assertEquals("SoundWave", createdProduct.getBrand());
        assertNotNull(createdProduct.getCreatedAt());
    }

    @Test
    void testGetProductByIdExists(){
        Product product = new Product(
            null, 
            "Headphones", 
            "Good description.",
            new BigDecimal("99.99"), 
            "Electronics", 
            150, 
            LocalDateTime.now(), 
            "SKU123456", 
            "SoundWave"
        );
        Product createdProduct = productService.createProduct(product);

        Product retrievedProduct = productService.getProductById(createdProduct.getId());

        assertNotNull(retrievedProduct, "Product should exist");
        assertEquals(createdProduct.getId(), retrievedProduct.getId());
        assertEquals(createdProduct.getName(), retrievedProduct.getName());
        assertEquals(createdProduct.getDescription(), retrievedProduct.getDescription());
        assertEquals(createdProduct.getPrice(), retrievedProduct.getPrice());
        assertEquals(createdProduct.getCategory(), retrievedProduct.getCategory());
        assertEquals(createdProduct.getStock(), retrievedProduct.getStock());
        assertEquals(createdProduct.getSku(), retrievedProduct.getSku());
        assertEquals(createdProduct.getBrand(), retrievedProduct.getBrand());
        assertEquals(createdProduct.getCreatedAt(), retrievedProduct.getCreatedAt());
    }    

    @Test
    void testGetProductByIdDoesNotExist() {
        Product retrievedProduct = productService.getProductById(999L); // ID 999 likely doesn't exist

        assertNull(retrievedProduct, "Product should not be found for ID 999");
    }

    @Test
    void testDeleteProductById(){
        Product product = new Product(
            null, 
            "Headphones", 
            "Good description.",
            new BigDecimal("99.99"), 
            "Electronics", 
            150, 
            LocalDateTime.now(), 
            "SKU123456", 
            "SoundWave"
        );
        Product createdProduct = productService.createProduct(product);

        assertNotNull(createdProduct.getId());
        
        Long productID = createdProduct.getId();
        productService.deleteProduct(productID);
        
        assertNull(productService.getProductById(productID));
    }

    @Test
    void testUpdateProduct(){
        Product product = new Product(
            null, 
            "New Headphones", 
            "Good description.",
            new BigDecimal("99.99"), 
            "Electronics", 
            150, 
            LocalDateTime.now(), 
            "SKU123456", 
            "SoundWave"
        );
        Product createdProduct = productService.createProduct(product);

        assertNotNull(createdProduct.getId());
        
        Product updatedProduct = new Product(
            null, 
            "New Headphones", 
            "Great description.",
            new BigDecimal("99.99"), 
            "Electronics", 
            150, 
            LocalDateTime.now(), 
            "SKU123456", 
            "SoundWave"
        );

        productService.updateProduct(createdProduct.getId(), updatedProduct);
        Product retrievedProduct = productService.getProductById(createdProduct.getId());

        assertNotNull(retrievedProduct, "Retrieved product should not be null after update");
        assertEquals(createdProduct.getId(), retrievedProduct.getId(), "Retrieved product ID should match original"); // ID should not change
        assertEquals(updatedProduct.getName(), retrievedProduct.getName(), "Name should be updated");
        assertEquals(updatedProduct.getDescription(), retrievedProduct.getDescription(), "Description should be updated");
        assertEquals(updatedProduct.getPrice(), retrievedProduct.getPrice(), "Price should be updated");
        assertEquals(updatedProduct.getStock(), retrievedProduct.getStock(), "Stock should be updated");
        assertEquals(updatedProduct.getSku(), retrievedProduct.getSku(), "SKU should be updated");
        assertEquals(updatedProduct.getBrand(), retrievedProduct.getBrand(), "Brand should be updated");
    }
}
