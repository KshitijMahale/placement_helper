package com.kshitij.placement_helper.controller;

import com.kshitij.placement_helper.model.User;
import com.kshitij.placement_helper.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.security.Principal;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String home() {
        return "login";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "logout", required = false) String logout, Model model) {
        if (logout != null) {
            model.addAttribute("logoutMessage", "You have been logged out.");
        }
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @Controller
    public class LogoutController {

        @GetMapping("/logout")
        public String logout(HttpServletRequest request, HttpServletResponse response) {
            // Perform logout (Spring Security does this automatically but you can customize it)
            SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
            logoutHandler.logout(request, response, null);

            return "redirect:/login?logout";
        }
    }

    @GetMapping("/userForm")
    public String showUserForm(Model model, OAuth2AuthenticationToken authentication) {
        OAuth2User oauthUser = authentication.getPrincipal();
        String email = oauthUser.getAttribute("email"); // e.g., kshitij.mahale24@spit.ac.in

        // Extract first part before dot or @
        String firstNameRaw = email.split("[.@]")[0]; // "kshitij"
        String firstName = firstNameRaw.substring(0, 1).toUpperCase() + firstNameRaw.substring(1).toLowerCase();

        // Try to find the user in the DB
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            // If not found, create a new user with defaults
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFirstName(firstName);
            newUser.setDegree("MCA");
            return newUser;
        });

        model.addAttribute("user", user);
        model.addAttribute("firstName", firstName); // Pass to view


        return "user-form";
    }


    @PostMapping("/saveUser")
    public String saveUser(User user, RedirectAttributes redirectAttributes) {
        // Check if user with same email exists
        Optional<User> existingUserOpt = userRepository.findByEmail(user.getEmail());

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            // update
            existingUser.setFirstName(user.getFirstName());
            existingUser.setDegree(user.getDegree());
            existingUser.setAcademicYear(user.getAcademicYear());
            existingUser.setDepartment(user.getDepartment());
            existingUser.setPassoutYear(user.getPassoutYear());

            userRepository.save(existingUser);
        } else {
            // if not found save as new
            userRepository.save(user);
        }

        redirectAttributes.addFlashAttribute("successMessage", "Your details have been saved successfully!");

        return "redirect:/dashboard";
    }

    @GetMapping("/exp-form")
    public String exp() {
        return "exp-form";
    }
}