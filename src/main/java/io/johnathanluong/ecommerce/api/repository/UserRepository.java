package io.johnathanluong.ecommerce.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.johnathanluong.ecommerce.api.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username); 
    boolean existsByUsername(String username);    
    boolean existsByEmail(String email); 
}
