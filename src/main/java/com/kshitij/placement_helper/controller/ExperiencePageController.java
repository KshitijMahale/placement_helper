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
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<String> company,
            @RequestParam(required = false) List<String> role,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer ctcMin,
            @RequestParam(required = false) Integer ctcMax,
            @RequestParam(required = false) Integer stipendMin,
            @RequestParam(required = false) Integer stipendMax,
            Model model) {

        List<InternshipExperience> experiences = experienceRepository.findAll().stream()
                .filter(exp -> (name == null || name.isEmpty() ||
                        (exp.getFullName() != null && exp.getFullName().toLowerCase().contains(name.toLowerCase()))))

                .filter(exp -> (company == null || company.isEmpty() ||
                        (exp.getCompany() != null && company.stream()
                                .anyMatch(c -> exp.getCompany().equalsIgnoreCase(c)))))

                .filter(exp -> (role == null || role.isEmpty() ||
                        (exp.getJobProfile() != null && role.stream()
                                .anyMatch(r -> exp.getJobProfile().equalsIgnoreCase(r)))))

                .filter(exp -> (type == null || type.isEmpty() ||
                        (exp.getOfferType() != null && exp.getOfferType().equalsIgnoreCase(type))))

                .filter(exp -> {
                    if (exp.getCtc() == null) return false;
                    if (ctcMin != null && exp.getCtc() < ctcMin) return false;
                    if (ctcMax != null && exp.getCtc() > ctcMax) return false;
                    return true;
                })

                .filter(exp -> {
                    if (stipendMin == null && stipendMax == null) return true;
                    if (exp.getInternshipStipend() == null) return false;
                    if (stipendMin != null && exp.getInternshipStipend() < stipendMin) return false;
                    if (stipendMax != null && exp.getInternshipStipend() > stipendMax) return false;
                    return true;
                })

                .collect(Collectors.toList());

        model.addAttribute("experiences", experiences);
        return "experience-details";
    }

    @GetMapping("/experience-browser/{id}")
    public InternshipExperience getExperienceById(@PathVariable Long id) {
        return experienceRepository.findById(id).orElse(null);
    }
}