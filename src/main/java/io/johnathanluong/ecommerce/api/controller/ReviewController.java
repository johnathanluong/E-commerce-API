package io.johnathanluong.ecommerce.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import io.johnathanluong.ecommerce.api.entity.Review;

public interface ReviewController {
    ResponseEntity<Review> createReview(Long productId, Review review, UserDetails userDetails);
    ResponseEntity<Review> getReviewById(Long id);
    ResponseEntity<List<Review>> getAllReviewsOfProduct(Long productId);
    ResponseEntity<Review> updateReview(Long id, Review updatedReview, UserDetails userDetails);
    ResponseEntity<Boolean> deleteReview(Long id, UserDetails userDetails);
}
