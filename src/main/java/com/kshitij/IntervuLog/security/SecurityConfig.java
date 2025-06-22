package com.kshitij.IntervuLog.security;

import com.kshitij.IntervuLog.model.User;
import com.kshitij.IntervuLog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/icon.png", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/", "/login").permitAll()  // Allow access to login page without authentication
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN", "SUPERADMIN")
                        .requestMatchers("/superadmin/**").hasRole("SUPERADMIN")
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
            OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(request);
            String email = oAuth2User.getAttribute("email");

            // If the email domain is not allowed, reject the login attempt
            if (!(email.endsWith("@spit.ac.in") || email.equals("mahalekshitij7@gmail.com") || email.equals("kshitijmahale02@gmail.com"))) {
                throw new OAuth2AuthenticationException("Unauthorized domain");
            }

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new OAuth2AuthenticationException("User not found"));

            // Add role as authority
            String role = "ROLE_" + user.getUserRole().name();
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);

            System.out.println("OAuth2 user email: " + email);
            System.out.println("User from DB: " + user);
            System.out.println("User role: " + user.getUserRole().name());

            return new DefaultOAuth2User(
                    Collections.singleton(authority),
                    oAuth2User.getAttributes(),
                    "email"
            );
//            return user;
        };
    }
}
