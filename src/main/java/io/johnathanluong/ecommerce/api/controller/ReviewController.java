package io.johnathanluong.ecommerce.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import io.johnathanluong.ecommerce.api.entity.Review;

public interface ReviewController {
    ResponseEntity<Review> createReview(Long productId, Review review);
    ResponseEntity<Review> getReviewById(Long id);
    ResponseEntity<List<Review>> getAllReviewsOfProduct(Long productId);
    ResponseEntity<Review> updateReview(Long id, Review updatedReview);
    ResponseEntity<Boolean> deleteReview(Long id);
}
