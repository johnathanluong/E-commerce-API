package io.johnathanluong.ecommerce.api.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.johnathanluong.ecommerce.api.entity.Product;
import io.johnathanluong.ecommerce.api.entity.Review;
import io.johnathanluong.ecommerce.api.service.ProductService;
import io.johnathanluong.ecommerce.api.service.ReviewService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api")
public class ReviewControllerImpl implements ReviewController {
    private final ReviewService reviewService;
    private final ProductService productService;
    
    public ReviewControllerImpl(ReviewService reviewService, ProductService productService){
        this.reviewService = reviewService;
        this.productService = productService;
    }
    
    @PostMapping("/products/{productId}/reviews")
    public ResponseEntity<Review> createReview(@PathVariable Long productId, @RequestBody Review review) {
        Product product = productService.getProductById(productId);
        review.setProduct(product);

        Review createdReview = reviewService.createReview(review);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/{id}")
            .buildAndExpand(createdReview.getId())
            .toUri();
        return ResponseEntity.created(location).body(createdReview);
    }

    @GetMapping("/products/{productId}/reviews")
    public ResponseEntity<List<Review>> getAllReviewsOfProduct(@PathVariable Long productId, Product product) {
        List<Review> reviews = reviewService.getAllReviewsOfProduct(product);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/reviews/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
        Review review = reviewService.getReviewById(id);
        return ResponseEntity.ok(review);
    }

    @PutMapping("/reviews/{id}")
    public ResponseEntity<Review> updateReview(@PathVariable Long id, Review updatedReview) {
        Review retrievedReview = reviewService.getReviewById(id);
        if(retrievedReview == null){
            return ResponseEntity.notFound().build();
        }

        Review newReview = reviewService.updateReview(id, updatedReview);
        return ResponseEntity.ok(newReview);
    }

    
    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Boolean> deleteReview(@PathVariable Long id) {
        boolean deleted = reviewService.deleteReview(id);

        if(deleted){
            return ResponseEntity.noContent().build();
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }
}
