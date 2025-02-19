package io.johnathanluong.ecommerce.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.johnathanluong.ecommerce.api.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>{
    
}
