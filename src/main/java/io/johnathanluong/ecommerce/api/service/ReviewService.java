package io.johnathanluong.ecommerce.api.service;

import java.util.List;

import io.johnathanluong.ecommerce.api.entity.Product;
import io.johnathanluong.ecommerce.api.entity.Review;


public interface ReviewService {
    Review createReview(Review review);
    Review getReviewById(Long id);
    List<Review> getAllReviewsOfProduct(Product product);
    Review updateReview(Long id, Review updatedReview);
    boolean deleteReview(Long id);
}
