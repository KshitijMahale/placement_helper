package com.kshitij.IntervuLog.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kshitij.IntervuLog.enums.ExperienceStatus;
import com.kshitij.IntervuLog.model.Company;
import com.kshitij.IntervuLog.model.InternshipExperience;
import com.kshitij.IntervuLog.model.Location;
import com.kshitij.IntervuLog.model.User;
import com.kshitij.IntervuLog.repository.CompanyRepository;
import com.kshitij.IntervuLog.repository.InternshipExperienceRepository;
import com.kshitij.IntervuLog.repository.LocationRepository;
import com.kshitij.IntervuLog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;

@Controller
public class InternshipSubmissionController {

    @Autowired
    private InternshipExperienceRepository experienceRepository;

    @Autowired
    private CompanyRepository companyRepo;

    @Autowired
    private LocationRepository locationRepo;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/submit-experience")
    public String handleInternshipSubmission(@RequestParam Map<String, String> requestParams, OAuth2AuthenticationToken authentication) {
        InternshipExperience experience = new InternshipExperience();

        // Get logged-in user's email
        OAuth2User oauth2User = authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Logged-in user not found in database.");
        }
//        System.out.println("Logged-in email: " + email);

        User user = optionalUser.get();
        experience.setSubmittedBy(user);
        experience.setStatus(ExperienceStatus.PENDING);

        experience.setFullName(requestParams.get("fullName"));
        experience.setCourse(requestParams.get("course"));
//        experience.setCompany(requestParams.get("company"));
        String companyName = requestParams.get("company");
        Company company = companyRepo.findByName(companyName);
        if (company == null) {
            company = new Company();
            company.setName(companyName);
            company = companyRepo.save(company);
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

//        experience.setLocation(requestParams.get("location"));
        String locationName = requestParams.get("location");
        Location location = locationRepo.findByName(locationName);
        if (location == null) {
            // create the location if it doesn't exist
            location = new Location();
            location.setName(locationName);
            location = locationRepo.save(location);
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

        experienceRepository.save(experience);
        return "redirect:/dashboard";
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
