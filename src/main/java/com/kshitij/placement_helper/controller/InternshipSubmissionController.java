package com.kshitij.placement_helper.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kshitij.placement_helper.model.InternshipExperience;
import com.kshitij.placement_helper.repository.InternshipExperienceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.*;

@Controller
public class InternshipSubmissionController {

    @Autowired
    private InternshipExperienceRepository experienceRepository;

    @PostMapping("/submit-experience")
    public String handleInternshipSubmission(@RequestParam Map<String, String> requestParams) {
        InternshipExperience experience = new InternshipExperience();

        experience.setFullName(requestParams.get("fullName"));
        experience.setCourse(requestParams.get("course"));
        experience.setCompany(requestParams.get("company"));
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

        experience.setLocation(requestParams.get("location"));

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
                questions.add(params.get("q" + roundIndex + "_" + questionIndex));
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
