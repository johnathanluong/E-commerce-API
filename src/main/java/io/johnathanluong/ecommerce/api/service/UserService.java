package io.johnathanluong.ecommerce.api.service;

import io.johnathanluong.ecommerce.api.entity.User;

public interface UserService {
    User createUser(User user);
    User getUserById(Long id);
    User getUserByEmail(String email);
    User getUserByUsername(String username);
    User updateUser(Long id, User updatedUser);
    boolean deleteUser(Long id);
}
