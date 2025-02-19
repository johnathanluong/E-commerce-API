package io.johnathanluong.ecommerce.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import io.johnathanluong.ecommerce.api.entity.Product;
import io.johnathanluong.ecommerce.api.repository.ProductRepository;
import io.johnathanluong.ecommerce.api.service.ProductServiceImpl;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
class ProductServiceImplTest {
    @Autowired
    private ProductServiceImpl productService;
    
    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAllInBatch();
    }

    
    @Test
    @DirtiesContext
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
    @DirtiesContext
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
        assertNotNull(createdProduct.getCreatedAt());
    }    

    @Test
    void testGetProductByIdDoesNotExist() {
        Product retrievedProduct = productService.getProductById(999L);

        assertNull(retrievedProduct, "Product should not be found for ID 999");
    }

    
    @Test
    @DirtiesContext
    void testUpdateProductExists(){
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
        
        assertNotNull(retrievedProduct);
        assertEquals(createdProduct.getId(), retrievedProduct.getId());
        assertEquals(updatedProduct.getName(), retrievedProduct.getName());
        assertEquals(updatedProduct.getDescription(), retrievedProduct.getDescription());
        assertEquals(updatedProduct.getPrice(), retrievedProduct.getPrice());
        assertEquals(updatedProduct.getStock(), retrievedProduct.getStock());
        assertEquals(updatedProduct.getSku(), retrievedProduct.getSku());
        assertEquals(updatedProduct.getBrand(), retrievedProduct.getBrand());
    }

    @Test
    void testUpdateProductNotExists(){
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

        Product newProduct = productService.updateProduct(9999L, updatedProduct);

        assertNull(newProduct);
    }
        
        
    @Test
    @DirtiesContext
    void testDeleteProductByIdExists(){
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
    void testDeleteProductByIdNotExists(){
        boolean deleted = productService.deleteProduct(9999L);

        assertFalse(deleted);
    }
}
