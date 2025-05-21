package com.kshitij.placement_helper.controller;

import com.kshitij.placement_helper.model.InternshipExperience;
import com.kshitij.placement_helper.repository.InternshipExperienceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ExperiencePageController {

    @Autowired
    private InternshipExperienceRepository experienceRepository;

    @GetMapping("/experience-browser")
    public String showExperiencePage(
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String course,
            @RequestParam(required = false) String job,
            Model model) {

        List<InternshipExperience> experiences = experienceRepository.findAll().stream()
                .filter(exp -> (company == null || company.isEmpty() ||
                        (exp.getCompany() != null && exp.getCompany().toLowerCase().contains(company.toLowerCase()))))
                .filter(exp -> (course == null || course.isEmpty() ||
                        (exp.getCourse() != null && exp.getCourse().toLowerCase().contains(course.toLowerCase()))))
                .filter(exp -> (job == null || job.isEmpty() ||
                        (exp.getJobProfile() != null && exp.getJobProfile().toLowerCase().contains(job.toLowerCase()))))
                .collect(Collectors.toList());

        model.addAttribute("experiences", experiences);
        return "experience-details";  // refers to experience-details.html
    }
    @GetMapping("/experience-browser/{id}")
    public InternshipExperience getExperienceById(@PathVariable Long id) {
        return experienceRepository.findById(id).orElse(null);
    }
}