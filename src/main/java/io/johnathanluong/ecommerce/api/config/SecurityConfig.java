package io.johnathanluong.ecommerce.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // I'll do it after i setup users
    // @Bean
    // public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    //     http
    //             .csrf().disable() //  <--  TEMPORARILY DISABLE CSRF PROTECTION (FOR TESTING ONLY)
    //             .authorizeHttpRequests((requests) -> requests
    //                     .requestMatchers("/api/products", "/api/products/102/reviews", "/api/reviews").permitAll()
    //                     .anyRequest().authenticated()
    //             );
    //     return http.build();
    // }
}
