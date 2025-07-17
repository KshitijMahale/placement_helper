package com.kshitij.IntervuLog.security;

import com.kshitij.IntervuLog.enums.UserRole;
import com.kshitij.IntervuLog.model.User;
import com.kshitij.IntervuLog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DataSource dataSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/health").permitAll()
                        .requestMatchers("/icon.png", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/login").permitAll()  // Allow access to login page without authentication
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
                .rememberMe(remember -> remember
                        .tokenRepository(persistentTokenRepository())
                        .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(7))
                        .key("yourUniqueAndSecureRememberMeKey")
                        .userDetailsService(this.userDetailsService())
                        .alwaysRemember(true)
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")         // Redirect to login after logout
                        .invalidateHttpSession(true)               // Invalidate session on logout
                        .deleteCookies("JSESSIONID", "remember-me") // Delete session cookie on logout
                );
        return http.build();
    }

    // Custom OAuth2UserService to validate Google account details
    private OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        return request -> {
            OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(request);
            String email = oAuth2User.getAttribute("email");

            // If the email domain is not allowed, reject the login attempt
            if (!(email.endsWith("@spit.ac.in") || email.equals("mahalekshitij7@gmail.com") || email.equals("kshitijmahale02@gmail.com") || email.equals("adityareddy.biz@gmail.com"))) {
                throw new OAuth2AuthenticationException("Unauthorized domain");
            }

            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setEmail(email);
                        newUser.setUserRole(UserRole.STUDENT); // default role
                        return userRepository.save(newUser);
                    });

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
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .map(user -> org.springframework.security.core.userdetails.User.builder()
                        .username(user.getEmail())
                        .password("") // Password is not used for OAuth2/RememberMe, but Spring Security requires it. Thats why we use empty
                        .authorities("ROLE_" + user.getUserRole().name())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }
}
