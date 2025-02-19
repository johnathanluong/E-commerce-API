package io.johnathanluong.ecommerce.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import io.johnathanluong.ecommerce.api.entity.Product;
import io.johnathanluong.ecommerce.api.entity.Review;
import io.johnathanluong.ecommerce.api.repository.ProductRepository;
import io.johnathanluong.ecommerce.api.repository.ReviewRepository;
import io.johnathanluong.ecommerce.api.service.ReviewServiceImpl;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
class ReviewServiceImplTest {
    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ReviewServiceImpl reviewService;

    Product product;

    @BeforeEach
    void setUp(){
        reviewRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();

        product = new Product(
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
        product = productRepository.save(product);
    }

    @Test
    @DirtiesContext
    void testCreateReview(){
        Review review = new Review(null, product, "SAMPLE TEXT", "POSITIVE");

        Review createdReview = reviewService.createReview(review);

        assertNotNull(createdReview);
        assertEquals(product, createdReview.getProduct());
        assertEquals("SAMPLE TEXT", createdReview.getReviewText());
        assertEquals("POSITIVE", createdReview.getSentiment());
        assertNotNull(createdReview.getId());
        assertNotNull(createdReview.getCreatedAt());
    }

    @Test
    @DirtiesContext
    void testGetReviewByIdExists(){
        Review review = new Review(null, product, "SAMPLE TEXT", "POSITIVE");
        Review createdReview = reviewService.createReview(review);
        
        Review foundReview = reviewService.getReviewById(createdReview.getId());

        assertNotNull(foundReview);
        assertEquals(createdReview.getId(), foundReview.getId());
        assertEquals(createdReview.getReviewText(), foundReview.getReviewText());
        assertEquals(createdReview.getSentiment(), foundReview.getSentiment());
        assertNotNull(foundReview.getCreatedAt());
    }

    @Test
    void testGetReviewByIdNotExists(){
        Review foundReview = reviewService.getReviewById(1234L);

        assertNull(foundReview);
    }

    @Test 
    @DirtiesContext
    void testGetAllReviewsByProductExists(){
        Review review1 = new Review(null, product, "SAMPLE TEXT1", "POSITIVE");
        Review review2 = new Review(null, product, "SAMPLE TEXT2", "NEGATIVE");
        
        reviewService.createReview(review1);
        reviewService.createReview(review2);

        List<Review> reviews = reviewService.getAllReviewsOfProduct(product); 

        assertNotNull(reviews);
        assertEquals(reviews.get(0).getReviewText(), review1.getReviewText());
        assertEquals(reviews.get(0).getSentiment(), review1.getSentiment());
        assertEquals(reviews.get(1).getReviewText(), review2.getReviewText());
        assertEquals(reviews.get(1).getSentiment(), review2.getSentiment());
    }

    @Test
    void testGetAllReviewsByProductNotExists(){
        List<Review> reviews = reviewService.getAllReviewsOfProduct(product); 

        assertEquals(0, reviews.size());
    }

    @Test
    @DirtiesContext
    void testUpdateReviewExists(){
        Review review1 = new Review(null, product, "SAMPLE TEXT1", "POSITIVE");
        Review createdReview = reviewService.createReview(review1);

        Review review2 = new Review(null, product, "SAMPLE TEXT2", "NEGATIVE");

        reviewService.updateReview(createdReview.getId(), review2);
        Review retrievedReview = reviewService.getReviewById(createdReview.getId());

        assertNotNull(retrievedReview);
        assertNotNull(retrievedReview.getId());
        assertNotNull(retrievedReview.getProduct());
        assertEquals("SAMPLE TEXT2", retrievedReview.getReviewText());
        assertEquals("NEGATIVE", retrievedReview.getSentiment());
    }

    @Test
    void testUpdateReviewNotExists(){
        Review review = new Review(null, product, "SAMPLE TEXT1", "POSITIVE");
        Review updatedReview = reviewService.updateReview(1234L, review);
        assertNull(updatedReview);
    }

    @Test
    @DirtiesContext
    void testDeleteReviewExists(){
        Review review = new Review(null, product, "SAMPLE TEXT1", "POSITIVE");
        Review createdReview = reviewService.createReview(review);
        Long reviewID = createdReview.getId();

        assertNotNull(createdReview);

        boolean deleted = reviewService.deleteReview(reviewID);
        
        assertTrue(deleted);
        assertNull(reviewService.getReviewById(reviewID));
    }

    @Test
    void testDeleteReviewNotExists(){
        boolean deleted = reviewService.deleteReview(1234L);
        assertFalse(deleted);
        assertNull(reviewService.getReviewById(1234L));
    }
}
