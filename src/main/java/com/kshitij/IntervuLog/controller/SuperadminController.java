package com.kshitij.IntervuLog.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kshitij.IntervuLog.enums.ExperienceStatus;
import com.kshitij.IntervuLog.enums.UserRole;
import com.kshitij.IntervuLog.model.Company;
import com.kshitij.IntervuLog.model.InternshipExperience;
import com.kshitij.IntervuLog.model.Location;
import com.kshitij.IntervuLog.model.User;
import com.kshitij.IntervuLog.repository.CompanyRepository;
import com.kshitij.IntervuLog.repository.InternshipExperienceRepository;
import com.kshitij.IntervuLog.repository.LocationRepository;
import com.kshitij.IntervuLog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/superadmin")
@PreAuthorize("hasRole('SUPERADMIN')")
public class SuperadminController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private InternshipExperienceRepository internshipExperienceRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long totalCompanies = companyRepository.count();
        long totalExperiences = internshipExperienceRepository.count();
        long totalUsers = userRepository.count();

        model.addAttribute("totalCompanies", totalCompanies);
        model.addAttribute("totalExperiences", totalExperiences);
        model.addAttribute("totalUsers", totalUsers);
        return "superadmin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              @RequestParam(required = false) String search,
                              Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pageable);
        if (search != null && !search.trim().isEmpty()) {
            userPage = userRepository.findByEmailContainingIgnoreCaseOrFirstNameContainingIgnoreCase(search, search, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("search", search);
        return "superadmin/manage-users";
    }

    @PostMapping("/users/update-role")
    public String updateUserRole(@RequestParam Long userId, @RequestParam String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setUserRole(UserRole.valueOf(newRole.toUpperCase()));
        userRepository.save(user);
        return "redirect:/superadmin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "User deleted successfully.");
        return "redirect:/superadmin/users";
    }

    @GetMapping("/add-experience")
    public String showExperienceForm(Model model) {
        model.addAttribute("experience", new InternshipExperience());
        List<Company> companies = companyRepository.findAll();
        companies.sort(Comparator.comparing(Company::getName));
        model.addAttribute("companies", companies);
        model.addAttribute("locations", locationRepository.findAll());
        return "superadmin/add-experience";
    }

    @PostMapping("/add-experience")
    public String submitExperience(@RequestParam Map<String, String> requestParams, OAuth2AuthenticationToken authentication) {
        InternshipExperience experience = new InternshipExperience();

        // Get logged-in user's email
        OAuth2User oauth2User = authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Logged-in user not found in database.");
        }

        User user = optionalUser.get();
        experience.setSubmittedBy(user);
        experience.setStatus(ExperienceStatus.APPROVED);

        experience.setFullName(requestParams.get("fullName"));
        experience.setCourse(requestParams.get("course"));
        String companyName = requestParams.get("company");
        Company company = companyRepository.findByName(companyName);
        if (company == null) {
            company = new Company();
            company.setName(companyName);
            company = companyRepository.save(company);
        }
        experience.setCompany(company);

        experience.setOtherCompany(requestParams.get("otherCompany"));
        experience.setJobProfile(requestParams.get("jobProfile"));
        experience.setOtherJobProfile(requestParams.get("otherJobProfile"));
        experience.setOfferType(requestParams.get("offerType"));

        String stipend = requestParams.get("internshipStipend");
        if (stipend != null && !stipend.isEmpty())
            experience.setInternshipStipend(Integer.valueOf(stipend));

        String ctc = requestParams.get("ctc");
        if (ctc != null && !ctc.isEmpty())
            experience.setCtc(Integer.valueOf(ctc));

        String locationName = requestParams.get("location");
        Location location = locationRepository.findByName(locationName);
        if (location == null) {
            // create the location if it doesn't exist
            location = new Location();
            location.setName(locationName);
            location = locationRepository.save(location);
        }
        experience.setLocation(location);

        String processDate = requestParams.get("processDate");
        if (processDate != null && !processDate.isBlank()) {
            experience.setProcessDate(LocalDate.parse(processDate));
        }

        experience.setLinkedin(requestParams.get("linkedin"));
        experience.setComments(requestParams.get("comments"));

        String roundsJson = extractRoundsJson(requestParams);
        experience.setRounds(roundsJson);

        internshipExperienceRepository.save(experience);
        return "redirect:/superadmin/add-experience";
    }

    private String extractRoundsJson(Map<String, String> params) {
        List<Map<String, Object>> rounds = new ArrayList<>();

        int roundIndex = 1;
        while (params.containsKey("roundType" + roundIndex)) {
            Map<String, Object> round = new HashMap<>();
            String roundType = params.get("roundType" + roundIndex);
            round.put("roundType", roundType);

            String otherTypeKey = "otherRoundType" + roundIndex;
            if ("Other".equals(roundType) && params.containsKey(otherTypeKey)) {
                round.put("otherRoundType", params.get(otherTypeKey));
            }

            List<String> questions = new ArrayList<>();
            int questionIndex = 1;
            while (params.containsKey("q" + roundIndex + "_" + questionIndex)) {
                String question = params.get("q" + roundIndex + "_" + questionIndex);
                question = question.replaceAll("(\r\n|\r|\n)", "<br/>");
                questions.add(question);
                questionIndex++;
            }

            round.put("questions", questions);
            rounds.add(round);
            roundIndex++;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(rounds);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "[]";
        }
    }
}
