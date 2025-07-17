package com.kshitij.IntervuLog.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kshitij.IntervuLog.enums.UserRole;
import com.kshitij.IntervuLog.model.*;
import com.kshitij.IntervuLog.repository.CompanyRepository;
import com.kshitij.IntervuLog.repository.InternshipExperienceRepository;
import com.kshitij.IntervuLog.repository.LocationRepository;
import com.kshitij.IntervuLog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.security.Principal;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepo;

    @Autowired
    private LocationRepository locationRepo;

    @Autowired
    private InternshipExperienceRepository internshipExperienceRepository;

    @GetMapping("/")
    public String home() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !(authentication.getPrincipal() instanceof String && "anonymousUser".equals(authentication.getPrincipal()))) {
            return "redirect:/dashboard";
        }
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
    public String dashboard(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch data for community impact
        long totalCompanies = companyRepo.count();
        long totalExperiences = internshipExperienceRepository.count();
        long totalUsers = userRepository.count();

        model.addAttribute("totalCompanies", formatDisplayNumber(totalCompanies));
        model.addAttribute("totalExperiences", formatDisplayNumber(totalExperiences));
        model.addAttribute("totalUsers", formatDisplayNumber(totalUsers));

        if (user == null) {
            return "redirect:/login";
        }

        switch (user.getUserRole()) {
            case SUPERADMIN:
                return "redirect:/superadmin/dashboard";
            case ADMIN:
                return "redirect:/admin/dashboard";
            case STUDENT:
            default:
                return "dashboard";
        }
    }
    private String formatDisplayNumber(long number) {
        if (number >= 1_000) {
            return (number / 1_000) + "K+";
        } else if (number >= 100) {
            return ((number / 10) * 10) + "+";
        } else if (number >= 5) {
            return ((number / 5) * 5) + "+";
        } else {
            return String.valueOf(number);
        }
    }

    @GetMapping("/userForm")
    public String showUserForm(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        String email = getUserEmail(principal);

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
            user.setUserRole(UserRole.STUDENT);
            userRepository.save(user);
        }

        redirectAttributes.addFlashAttribute("successMessage", "Your details have been saved successfully!");

        return "redirect:/dashboard";
    }

    @GetMapping("/exp-form")
    public String viewUserExperience(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = getUserEmail(principal);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<InternshipExperience> experiences = internshipExperienceRepository.findBySubmittedBy(user);

        List<Company> companies = companyRepo.findAll();
        companies.sort(Comparator.comparing(
                (Company c) -> c.getName().equalsIgnoreCase("Other") ? 1 : 0
        ).thenComparing(Company::getName));
        model.addAttribute("companies", companies);

        List<Location> locations = locationRepo.findAll();
        model.addAttribute("locations", locations);

        if (!experiences.isEmpty()) {
            ObjectMapper objectMapper = new ObjectMapper();

            for (InternshipExperience experience : experiences) {
                try {
                    List<Round> parsedRounds = objectMapper.readValue(
                            experience.getRounds(),
                            new TypeReference<List<Round>>() {}
                    );
                    experience.setParsedRounds(parsedRounds);
                } catch (Exception e) {
                    e.printStackTrace(); // log the issue
                }
            }
            model.addAttribute("experiences", experiences);
        } else {
            model.addAttribute("noExperience", true);
        }

        return "exp-form";

    }
    @GetMapping("/contact")
    public String contactPage() {
        return "contact";
    }

    private String getUserEmail(Principal principal) {
        if (principal == null) {
            // This case should ideally not happen if Spring Security's .anyRequest().authenticated() is working
            // but it's good to handle defensively, perhaps throw an exception or return null based on context.
            // For now, consistent with your existing logic, we'll assume a non-null principal for email extraction.
            return null; // Or throw new IllegalArgumentException("Principal cannot be null");
        }

        String email;
        if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2User oauth2User = (OAuth2User) ((OAuth2AuthenticationToken) principal).getPrincipal();
            email = oauth2User.getAttribute("email");
        } else if (principal instanceof org.springframework.security.core.userdetails.User) {
            email = principal.getName();
        } else {
            email = principal.getName();
        }
        return email;
    }

}