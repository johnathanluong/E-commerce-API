package io.johnathanluong.ecommerce.api.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.johnathanluong.ecommerce.api.entity.Product;
import io.johnathanluong.ecommerce.api.entity.Review;
import io.johnathanluong.ecommerce.api.entity.User;
import io.johnathanluong.ecommerce.api.exception.AuthorizationException;
import io.johnathanluong.ecommerce.api.service.ProductService;
import io.johnathanluong.ecommerce.api.service.ReviewService;
import io.johnathanluong.ecommerce.api.service.UserService;

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
    private final UserService userService;
    
    public ReviewControllerImpl(ReviewService reviewService, ProductService productService, UserService userService){
        this.reviewService = reviewService;
        this.productService = productService;
        this.userService = userService;
    }
    
    @PostMapping("/products/{productId}/reviews")
    public ResponseEntity<Review> createReview(@PathVariable Long productId, @RequestBody Review review, @AuthenticationPrincipal UserDetails userDetails) {
        Product product = productService.getProductById(productId);
        User user = userService.getUserByUsername(userDetails.getUsername());

        if(user == null || product == null){
            return ResponseEntity.notFound().build();
        }
        review.setProduct(product);
        review.setUser(user);
        Review createdReview = reviewService.createReview(review, user);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/api/reviews/{id}")
            .buildAndExpand(createdReview.getId())
            .toUri();
        
        return ResponseEntity.created(location).body(createdReview);
    }

    @GetMapping("/products/{productId}/reviews")
    public ResponseEntity<List<Review>> getAllReviewsOfProduct(@PathVariable Long productId) {
        Product product = productService.getProductById(productId);
        if(product == null){
            return ResponseEntity.notFound().build();
        }

        List<Review> reviews = reviewService.getAllReviewsOfProduct(product);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/reviews/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
        Review review = reviewService.getReviewById(id);
        if(review == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(review);
    }

    @PutMapping("/reviews/{id}")
    public ResponseEntity<Review> updateReview(@PathVariable Long id, Review updatedReview, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try{    
            Review newReview = reviewService.updateReview(id, updatedReview, user);
            return ResponseEntity.ok(newReview);
        }
        catch(AuthorizationException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    
    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Boolean> deleteReview(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try{
            reviewService.deleteReview(id, user);
            return ResponseEntity.noContent().build();
        }
        catch(AuthorizationException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        } 
    }
}
