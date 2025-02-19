package io.johnathanluong.ecommerce.api.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.johnathanluong.ecommerce.api.entity.User;
import io.johnathanluong.ecommerce.api.exception.EmailAlreadyExistsException;
import io.johnathanluong.ecommerce.api.exception.UsernameAlreadyExistsException;
import io.johnathanluong.ecommerce.api.service.UserService;
import jakarta.validation.ValidationException;

@RestController
@RequestMapping("/api/users")
public class UserControllerImpl implements UserController {
    private UserService userService;

    public UserControllerImpl(UserService userService){
        this.userService = userService;
    }


    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try{
            User createdUser = userService.createUser(user);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();
            return ResponseEntity.created(location).body(createdUser);
        } catch(UsernameAlreadyExistsException | EmailAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch(ValidationException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User user = userService.getUserByEmail(email);
        if(user == null){
            return ResponseEntity.notFound().build();
        } 
        
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if(user == null){
            return ResponseEntity.notFound().build();
        } 
        
        return ResponseEntity.ok(user);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        if(user == null){
            return ResponseEntity.notFound().build();
        } 
        
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        try{
            User user = userService.updateUser(id, updatedUser);
            if(user == null){
                return ResponseEntity.notFound().build();
            }
            else{
                return ResponseEntity.ok(user);
            }
        }
        catch(ValidationException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        if(deleted){
            return ResponseEntity.noContent().build();
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }
}
