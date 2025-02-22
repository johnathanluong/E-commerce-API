package io.johnathanluong.ecommerce.api.service;

import java.util.List;

import io.johnathanluong.ecommerce.api.entity.Product;
import io.johnathanluong.ecommerce.api.entity.Review;
import io.johnathanluong.ecommerce.api.entity.User;


public interface ReviewService {
    Review createReview(Review review, User user);
    Review getReviewById(Long id);
    List<Review> getAllReviewsOfProduct(Product product);
    Review updateReview(Long id, Review updatedReview, User user);
    void deleteReview(Long id, User user);
}
