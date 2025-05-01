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

import java.security.Principal;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String home() {
        return "index";
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


        User user = new User();
        user.setEmail(email); // Now this is the correct email
        user.setFirstName(firstName);
        user.setDegree("MCA");

        model.addAttribute("user", user);
        model.addAttribute("firstName", firstName); // Pass to view


        return "user-form";
    }


    // Handle form submission
    @PostMapping("/saveUser")
    public String saveUser(User user) {
        userRepository.save(user);  // Save the user details in the database
        return "redirect:/userForm?success";  // Redirect to the form page with success message
    }
}