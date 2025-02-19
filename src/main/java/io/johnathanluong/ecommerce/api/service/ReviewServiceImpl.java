package io.johnathanluong.ecommerce.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import io.johnathanluong.ecommerce.api.entity.Product;
import io.johnathanluong.ecommerce.api.entity.Review;
import io.johnathanluong.ecommerce.api.repository.ReviewRepository;

@Service
public class ReviewServiceImpl implements ReviewService{
    private final ReviewRepository reviewRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository){
        this.reviewRepository = reviewRepository;
    }

    @Override
    public Review createReview(Review review) {
        review.setCreatedAt(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    @Override
    public List<Review> getAllReviewsOfProduct(Product product) {
        return reviewRepository.findAllByProduct(product);
    }

    @Override
    public Review getReviewById(Long id) {
        Optional<Review> review = reviewRepository.findById(id);
        if(review.isPresent()){
            return review.get();
        }
        
        return null;
    }
    
    @Override
    public Review updateReview(Long id, Review updatedReview) {
        Optional<Review> review = reviewRepository.findById(id);
        if(review.isPresent()){
            Review existingReview = review.get();
            if(updatedReview.getReviewText() != null)
                existingReview.setReviewText(updatedReview.getReviewText());
            if(updatedReview.getProduct() != null)
                existingReview.setProduct(updatedReview.getProduct());
            if(updatedReview.getSentiment() != null)
                existingReview.setSentiment(updatedReview.getSentiment());
            return reviewRepository.save(existingReview);
        }
        return null;
    }
    
    @Override
    public boolean deleteReview(Long id) {
        if(reviewRepository.existsById(id)){
            reviewRepository.deleteById(id);
            return true;
        }

        return false;
    }
    
}
