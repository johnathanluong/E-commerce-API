package io.johnathanluong.ecommerce.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.johnathanluong.ecommerce.api.controller.ReviewControllerImpl;
import io.johnathanluong.ecommerce.api.entity.Product;
import io.johnathanluong.ecommerce.api.entity.Review;
import io.johnathanluong.ecommerce.api.service.ProductService;
import io.johnathanluong.ecommerce.api.service.ReviewService;

@WebMvcTest(value = ReviewControllerImpl.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class ReviewControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    
    @MockitoBean
    ReviewService reviewService;
    @MockitoBean
    ProductService productService;
    
    @Test
    void testCreateReview_ProductExists() throws Exception {
        Long productId = 123L;
        Product mockProduct = new Product();
        mockProduct.setId(productId);

        Review reviewToCreate = new Review();
        reviewToCreate.setReviewText("SAMPLE TEXT");
        reviewToCreate.setSentiment("POSITIVE");

        Review createdReview = new Review(); 
        createdReview.setId(456L);
        createdReview.setReviewText(reviewToCreate.getReviewText());
        createdReview.setSentiment(reviewToCreate.getSentiment());
        createdReview.setProduct(mockProduct);

        when(productService.getProductById(productId)).thenReturn(mockProduct);
        when(reviewService.createReview(reviewToCreate)).thenReturn(createdReview);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/products/{productId}/reviews", productId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(reviewToCreate)))
                            .andExpect(status().isCreated())
                            .andExpect(header().string("Location", "http://localhost/api/reviews/456"))
                            .andExpect(jsonPath("$.reviewText").value("SAMPLE TEXT"))
                            .andExpect(jsonPath("$.sentiment").value("POSITIVE"));
    }
    @Test
    void testCreateReview_ProductNotExists() throws Exception {
        Long productId = 123L;
        Review reviewToCreate = new Review();

        when(productService.getProductById(productId)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/products/{productId}/reviews", productId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(reviewToCreate)))
                            .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllReviewsOfProduct_ProductExists() throws Exception {
        Long productId = 123L;
        Product mockProduct = new Product();
        mockProduct.setId(productId);

        Review review1 = new Review();
        review1.setId(1L);
        review1.setReviewText("Review 1");
        Review review2 = new Review();
        review2.setId(2L);
        review2.setReviewText("Review 2");
        List<Review> reviews = Arrays.asList(review1, review2);

        when(productService.getProductById(productId)).thenReturn(mockProduct);
        when(reviewService.getAllReviewsOfProduct(mockProduct)).thenReturn(reviews);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/{productId}/reviews", productId)
                                    .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$[0].id").value(1))
                        .andExpect(jsonPath("$[0].reviewText").value("Review 1"))
                        .andExpect(jsonPath("$[1].id").value(2))
                        .andExpect(jsonPath("$[1].reviewText").value("Review 2"));
    }

    @Test
    void testGetAllReviewsOfProduct_ProductExistsNoReviews() throws Exception {
        Long productId = 123L;
        Product mockProduct = new Product();
        mockProduct.setId(productId);

        when(productService.getProductById(productId)).thenReturn(mockProduct);
        when(reviewService.getAllReviewsOfProduct(mockProduct)).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/{productId}/reviews", productId)
                                    .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testGetAllReviewsOfProduct_ProductNotExists() throws Exception {
        Long productId = 123L;

        when(productService.getProductById(productId)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/{productId}/reviews", productId)
                                    .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound());
    }

    @Test
    void testGetReviewById_ReviewExists() throws Exception {
        Long reviewId = 456L;
        Review mockReview = new Review();
        mockReview.setId(reviewId);
        mockReview.setReviewText("Existing Review");
        mockReview.setSentiment("NEGATIVE");

        when(reviewService.getReviewById(reviewId)).thenReturn(mockReview);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/reviews/{id}", reviewId)
                                    .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.id").value(reviewId))
                        .andExpect(jsonPath("$.reviewText").value("Existing Review"))
                        .andExpect(jsonPath("$.sentiment").value("NEGATIVE"));
    }

    @Test
    void testGetReviewById_ReviewNotExists() throws Exception {
        Long reviewId = 4567L;

        when(reviewService.getReviewById(reviewId)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/reviews/{id}", reviewId)
                                    .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateReview_ReviewExists() throws Exception {
        Long reviewId = 456L;
        Review existingReview = new Review();
        existingReview.setId(reviewId);
        existingReview.setReviewText("Original Review Text");
        existingReview.setSentiment("NEGATIVE");

        Review updatedReview = new Review();
        updatedReview.setReviewText("Updated Review Text");
        updatedReview.setSentiment("POSITIVE");

        Review serviceUpdatedReview = new Review();
        serviceUpdatedReview.setId(reviewId);
        serviceUpdatedReview.setReviewText(updatedReview.getReviewText());
        serviceUpdatedReview.setSentiment(updatedReview.getSentiment());

        when(reviewService.getReviewById(reviewId)).thenReturn(existingReview);
        when(reviewService.updateReview(eq(reviewId), any(Review.class))).thenReturn(serviceUpdatedReview);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/reviews/{id}", reviewId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(updatedReview)))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.id").value(reviewId))
                        .andExpect(jsonPath("$.reviewText").value("Updated Review Text"))
                        .andExpect(jsonPath("$.sentiment").value("POSITIVE"));
    }

    @Test
    void testUpdateReview_ReviewNotExist() throws Exception {
        Long reviewId = 456L;
        Review updatedReview = new Review();

        when(reviewService.getReviewById(reviewId)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/reviews/{id}", reviewId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(updatedReview)))
                        .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteReview_ReviewExists() throws Exception {
        Long reviewId = 456L;

        when(reviewService.deleteReview(reviewId)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/reviews/{id}", reviewId))
                        .andExpect(status().isNoContent());

        verify(reviewService, times(1)).deleteReview(reviewId);
    }

    @Test
    void testDeleteReview_ReviewNotExist() throws Exception {
        Long reviewId = 456L;

        when(reviewService.deleteReview(reviewId)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/reviews/{id}", reviewId))
                        .andExpect(status().isNotFound());

        verify(reviewService, times(1)).deleteReview(reviewId);
    }
}
