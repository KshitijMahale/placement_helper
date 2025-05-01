package com.kshitij.placement_helper.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/userForm").permitAll()  // Allow access to login page without authentication
                        .anyRequest().authenticated()              // Require authentication for other pages
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")                       // Custom login page
                        .userInfoEndpoint(user -> user.userService(this.oauth2UserService()))  // Custom user info service
                        .defaultSuccessUrl("/dashboard", true)     // Redirect to /dashboard after successful login
                        .failureUrl("/login?error")                // Redirect to login with error on failure
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")         // Redirect to login after logout
                        .invalidateHttpSession(true)               // Invalidate session on logout
                        .deleteCookies("JSESSIONID")               // Delete session cookie on logout
                );
        return http.build();
    }

    // Custom OAuth2UserService to validate Google account details
    private OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        return request -> {
            OAuth2User user = new DefaultOAuth2UserService().loadUser(request);
            String email = user.getAttribute("email");

            // If the email domain is not allowed, reject the login attempt
            if (!email.endsWith("@spit.ac.in")) {
                throw new OAuth2AuthenticationException("Unauthorized domain");
            }

            return user;
        };
    }
}
