package io.johnathanluong.ecommerce.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.johnathanluong.ecommerce.api.entity.Product;
import io.johnathanluong.ecommerce.api.entity.Review;
import java.util.List;


@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>{
    List<Review> findAllByProduct(Product product);
}
