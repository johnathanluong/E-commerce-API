package io.johnathanluong.ecommerce.api.controller;

import org.springframework.http.ResponseEntity;

import io.johnathanluong.ecommerce.api.entity.User;

public interface UserController {
    ResponseEntity<User> createUser(User user);
    ResponseEntity<User> getUserById(Long id);
    ResponseEntity<User> getUserByEmail(String email);
    ResponseEntity<User> getUserByUsername(String username);
    ResponseEntity<User> updateUser(Long id, User updatedUser);
    ResponseEntity<Boolean> deleteUser(Long id);
}
