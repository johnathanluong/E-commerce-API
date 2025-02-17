package io.johnathanluong.ecommerce.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.johnathanluong.ecommerce.api.controller.ProductControllerImpl;
import io.johnathanluong.ecommerce.api.entity.Product;
import io.johnathanluong.ecommerce.api.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

// Excluding security because only want to test the controller functionality
@WebMvcTest(value = ProductControllerImpl.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class ProductControllerTest {
    
    // Mocks HTTP requests
    @Autowired
    MockMvc mockMvc;
    
    @Autowired
    ObjectMapper objectMapper;
    
    @MockitoBean
    ProductService productService;
    
    @Test
    void testCreateProduct() throws Exception{
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

        Product createdProduct = new Product();
        createdProduct.setId(1L);
        createdProduct.setName(product.getName());
        createdProduct.setDescription(product.getDescription());
        createdProduct.setPrice(product.getPrice());
        createdProduct.setCategory(product.getCategory());
        createdProduct.setStock(product.getStock());
        createdProduct.setCreatedAt(product.getCreatedAt());
        createdProduct.setSku(product.getSku());
        createdProduct.setBrand(product.getBrand());


        when(productService.createProduct(product)).thenReturn(createdProduct);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Reverted to JSON content type assertion
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Headphones"))
                .andExpect(jsonPath("$.description").value("Good description."))
                .andExpect(jsonPath("$.price").value(99.99))
                .andExpect(jsonPath("$.category").value("Electronics"))
                .andExpect(jsonPath("$.stock").value(150));
    }

    @Test
    void testGetProductByIdExists() throws Exception{
        Product createdProduct = new Product();
        createdProduct.setId(1L);
        createdProduct.setName("Headphones");
        createdProduct.setDescription("Good description.");
        createdProduct.setPrice(new BigDecimal("99.99"));
        createdProduct.setCategory("Electronics");
        createdProduct.setStock(150);
        createdProduct.setSku("SKU123456");
        createdProduct.setBrand("SoundWave");

        when(productService.getProductById(1L)).thenReturn(createdProduct);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/1")
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.id").value(1))
                        .andExpect(jsonPath("$.id").value(1))
                        .andExpect(jsonPath("$.name").value("Headphones"))
                        .andExpect(jsonPath("$.description").value("Good description."))
                        .andExpect(jsonPath("$.price").value(99.99))
                        .andExpect(jsonPath("$.category").value("Electronics"))
                        .andExpect(jsonPath("$.stock").value(150))
                        .andExpect(jsonPath("$.sku").value("SKU123456"))
                        .andExpect(jsonPath("$.brand").value("SoundWave"));
    }

    @Test
    void testGetProductByIdNotExist() throws Exception{
        when(productService.getProductById(10L)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/10")
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andExpect(content().string(""));
    }

    @Test
    void testGetAllProductsExists() throws Exception{
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Headphones");
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Keyboard");
        List<Product> productList = Arrays.asList(product1, product2);

        when(productService.getAllProducts()).thenReturn(productList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Headphones"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Keyboard"));
    }

    @Test
    void testGetAllProductsNotExist() throws Exception{
        when(productService.getAllProducts()).thenReturn(Arrays.asList());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testUpdateProductExists() throws Exception {
        // Previous data
        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Original Headphones");
        existingProduct.setDescription("Old description.");

        // New data
        Product updatedProduct = new Product();
        updatedProduct.setName("New Headphones"); 
        updatedProduct.setDescription("New description.");
        updatedProduct.setPrice(new BigDecimal("129.99"));
        updatedProduct.setCategory("Electronics");
        updatedProduct.setStock(100);
        updatedProduct.setSku("SKU789123");
        updatedProduct.setBrand("UpdatedBrand");
        
        // Returned after update
        Product serviceUpdatedProduct = new Product(); 
        serviceUpdatedProduct.setId(1L); 
        serviceUpdatedProduct.setName(updatedProduct.getName());
        serviceUpdatedProduct.setDescription(updatedProduct.getDescription());
        serviceUpdatedProduct.setPrice(updatedProduct.getPrice());
        serviceUpdatedProduct.setCategory(updatedProduct.getCategory());
        serviceUpdatedProduct.setStock(updatedProduct.getStock());
        serviceUpdatedProduct.setSku(updatedProduct.getSku());
        serviceUpdatedProduct.setBrand(updatedProduct.getBrand());
        
        when(productService.getProductById(1L)).thenReturn(existingProduct);
        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(serviceUpdatedProduct);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(serviceUpdatedProduct.getId()))
                    .andExpect(jsonPath("$.name").value(serviceUpdatedProduct.getName()))
                    .andExpect(jsonPath("$.description").value(serviceUpdatedProduct.getDescription()))
                    .andExpect(jsonPath("$.price").value(serviceUpdatedProduct.getPrice()))
                    .andExpect(jsonPath("$.category").value(serviceUpdatedProduct.getCategory()))
                    .andExpect(jsonPath("$.price").value(serviceUpdatedProduct.getPrice()))
                    .andExpect(jsonPath("$.stock").value(serviceUpdatedProduct.getStock()))
                    .andExpect(jsonPath("$.sku").value(serviceUpdatedProduct.getSku()))
                    .andExpect(jsonPath("$.brand").value(serviceUpdatedProduct.getBrand()));
    }

    @Test
    void testUpdateProductNotExists() throws Exception{
        Product updatedProduct = new Product();
        updatedProduct.setName("New Headphones"); 
        updatedProduct.setDescription("New description.");
        updatedProduct.setPrice(new BigDecimal("129.99"));
        updatedProduct.setCategory("Electronics");
        updatedProduct.setStock(100);
        updatedProduct.setSku("SKU789123");
        updatedProduct.setBrand("UpdatedBrand");

        when(productService.updateProduct(10L, updatedProduct)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/products/10")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProduct)))
            .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteProductByIdExists() throws Exception{
        when(productService.deleteProduct(1L)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/products/1"))
                        .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    void testDeleteProductByIdNotExists() throws Exception{
        when(productService.deleteProduct(10L)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/products/10"))
                        .andExpect(status().isNotFound());

        verify(productService, times(1)).deleteProduct(10L);
    }
}
