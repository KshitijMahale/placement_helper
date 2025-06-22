package com.kshitij.IntervuLog.controller;

import com.kshitij.IntervuLog.model.InternshipExperience;
import com.kshitij.IntervuLog.repository.InternshipExperienceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ExperienceApiController {

    @Autowired
    private InternshipExperienceRepository experienceRepository;

    /**
     * Get filtered internship experiences based on optional parameters: company, course, and job profile.
     */
    @GetMapping("/experience-browser")
    public List<InternshipExperience> getFilteredExperiences(
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String course,
            @RequestParam(required = false) String job
    ) {
        return experienceRepository.findAll().stream()
                .filter(exp -> (company == null || company.isEmpty() ||
                        (exp.getCompany() != null && exp.getCompany().getName().toLowerCase().contains(company.toLowerCase()))))
                .filter(exp -> (course == null || course.isEmpty() ||
                        (exp.getCourse() != null && exp.getCourse().toLowerCase().contains(course.toLowerCase()))))
                .filter(exp -> (job == null || job.isEmpty() ||
                        (exp.getJobProfile() != null && exp.getJobProfile().toLowerCase().contains(job.toLowerCase()))))
                .collect(Collectors.toList());
    }

    /**
     * Get a single internship experience by its ID.
     */
    @GetMapping("/experience-browser/{id}")
    public InternshipExperience getExperienceById(@PathVariable Long id) {
        return experienceRepository.findById(id).orElse(null);
    }
}
