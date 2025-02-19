package io.johnathanluong.ecommerce.api.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.johnathanluong.ecommerce.api.entity.User;
import io.johnathanluong.ecommerce.api.exception.EmailAlreadyExistsException;
import io.johnathanluong.ecommerce.api.exception.UsernameAlreadyExistsException;
import io.johnathanluong.ecommerce.api.repository.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;

@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final Validator validator;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, Validator validator, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.validator = validator;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(User user) {
        validateUser(user);

        if(userRepository.existsByUsername(user.getUsername())){
            throw new UsernameAlreadyExistsException("Username already in use.");
        }
        if(userRepository.existsByEmail(user.getEmail())){
            throw new EmailAlreadyExistsException("Email already in use.");
        }

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public User getUserByEmail(String email) {
        Optional<User> findUser = userRepository.findByEmail(email);
        return findUser.orElse(null);

    }

    @Override
    public User getUserById(Long id) {
        Optional<User> findUser = userRepository.findById(id);
        return findUser.orElse(null);

    }

    @Override
    public User getUserByUsername(String username) {
        Optional<User> findUser = userRepository.findByUsername(username);
        return findUser.orElse(null);
    }

    @Override
    public User updateUser(Long id, User updatedUser) {
        Optional<User> findUser = userRepository.findById(id);
        if(findUser.isPresent()){
            User user = findUser.get();
            if(updatedUser.getUsername() != null)
                user.setUsername(updatedUser.getUsername());
            if(updatedUser.getEmail() != null)
                user.setEmail(updatedUser.getEmail());

            validateUser(user);

            if(updatedUser.getPassword() != null){ // Password handling only if validation passed
                String hashedPassword = passwordEncoder.encode(updatedUser.getPassword());
                user.setPassword(hashedPassword);
            }

            return userRepository.save(user);
        }
        return null;
    }
    
    @Override
    public boolean deleteUser(Long id) {
        if(userRepository.existsById(id)){
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void validateUser(User user){
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if(!violations.isEmpty()){
            StringBuilder error = new StringBuilder();
            for(ConstraintViolation<User> v : violations){
                error.append(v.getMessage()).append(". ");
            }

            throw new ValidationException("User validation failed: " + error.toString());
        }
    }
}
