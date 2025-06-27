package com.kshitij.IntervuLog.config;

import com.kshitij.IntervuLog.model.User;
import com.kshitij.IntervuLog.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.security.Principal;
import java.util.Optional;

@Component
public class ProfileCompletionInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;

    public ProfileCompletionInterceptor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            return true; // user not logged in
        }

        String email = principal.getName();
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            boolean incomplete = user.getFirstName() == null ||
                    user.getDepartment() == null ||
                    user.getDegree() == null ||
                    user.getPassoutYear() == null ||
                    user.getAcademicYear() == null;

            String uri = request.getRequestURI();

            if (incomplete &&
                    !uri.startsWith("/dashboard") &&
                    !uri.startsWith("/userForm") &&
                    !uri.startsWith("/saveUser") &&
                    !uri.startsWith("/logout")) {
                response.sendRedirect("/userForm");
                return false;
            }

        }

        return true;
    }
}
