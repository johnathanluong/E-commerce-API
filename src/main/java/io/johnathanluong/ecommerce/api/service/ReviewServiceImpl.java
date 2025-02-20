package io.johnathanluong.ecommerce.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import io.johnathanluong.ecommerce.api.entity.Product;
import io.johnathanluong.ecommerce.api.entity.Review;
import io.johnathanluong.ecommerce.api.entity.User;
import io.johnathanluong.ecommerce.api.exception.AuthorizationException;
import io.johnathanluong.ecommerce.api.repository.ReviewRepository;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.comprehend.ComprehendClient;
import software.amazon.awssdk.services.comprehend.model.DetectSentimentRequest;
import software.amazon.awssdk.services.comprehend.model.DetectSentimentResponse;
import software.amazon.awssdk.services.comprehend.model.SentimentType;

@Service
public class ReviewServiceImpl implements ReviewService{
    private final ReviewRepository reviewRepository;
    private final ComprehendClient comprehendClient;

    public ReviewServiceImpl(ReviewRepository reviewRepository){
        this.reviewRepository = reviewRepository;
        this.comprehendClient = ComprehendClient.builder().region(Region.US_EAST_1).build();
    }

    @Override
    public Review createReview(Review review, User user) {
        review.setCreatedAt(LocalDateTime.now());
        review.setUser(user);

        DetectSentimentRequest detectSentimentRequest = DetectSentimentRequest.builder()
            .text(review.getReviewText())
            .languageCode("en")
            .build();
        
        DetectSentimentResponse detectSentimentResponse = comprehendClient.detectSentiment(detectSentimentRequest);
        SentimentType sentimentType = detectSentimentResponse.sentiment();
        review.setSentiment(sentimentType.toString());
        
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
    public Review updateReview(Long id, Review updatedReview, User currentUser) {
        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));

        if (!isReviewOwner(existingReview, currentUser)) {
            throw new AuthorizationException("You are not authorized to edit this review.");
        }

        if(updatedReview.getReviewText() != null)
            existingReview.setReviewText(updatedReview.getReviewText());

        if (updatedReview.getReviewText() != null) {
            DetectSentimentRequest detectSentimentRequest = DetectSentimentRequest.builder()
                    .text(existingReview.getReviewText())
                    .languageCode("en")
                    .build();
            DetectSentimentResponse detectSentimentResponse = comprehendClient.detectSentiment(detectSentimentRequest);
            SentimentType sentimentType = detectSentimentResponse.sentiment();
            existingReview.setSentiment(sentimentType.toString());
        } else if (updatedReview.getSentiment() != null) {
            existingReview.setSentiment(updatedReview.getSentiment());
        }


        return reviewRepository.save(existingReview);
    }
    
    @Override
    public void deleteReview(Long id, User user) {
        Review review = reviewRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));
        
        if(!isReviewOwner(review, user)){
            throw new AuthorizationException("You are not authorized to delete this review.");
        }
        
        reviewRepository.delete(review);
    }
    
    private boolean isReviewOwner(Review review, User user) {
        return review.getUser().getId().equals(user.getId());
    }
}
