package com.kshitij.placement_helper.controller;

import com.kshitij.placement_helper.model.Company;
import com.kshitij.placement_helper.model.InternshipExperience;
import com.kshitij.placement_helper.model.Location;
import com.kshitij.placement_helper.repository.CompanyRepository;
import com.kshitij.placement_helper.repository.InternshipExperienceRepository;
import com.kshitij.placement_helper.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class ExperiencePageController {

    @Autowired
    private InternshipExperienceRepository experienceRepository;

    @Autowired
    private CompanyRepository companyRepo;

    @Autowired
    private LocationRepository locationRepo;

    @GetMapping("/experience-browser")
    public String showExperiencePage(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<String> company,
            @RequestParam(required = false) List<String> role,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer ctcMin,
            @RequestParam(required = false) Integer ctcMax,
            @RequestParam(required = false) Integer stipendMin,
            @RequestParam(required = false) Integer stipendMax,
            Model model) {

        // Set curr year as default if not provided
        if (year == null) {
            year = Year.now().getValue();
        }

        final int selectedYear = year;

        List<InternshipExperience> experiences = experienceRepository.findAll().stream()
                .filter(exp -> exp.getProcessDate() != null && exp.getProcessDate().getYear() == selectedYear)

                .filter(exp -> (name == null || name.isEmpty() ||
                        (exp.getFullName() != null && exp.getFullName().toLowerCase().contains(name.toLowerCase()))))

                .filter(exp -> {
                    if (company == null || company.isEmpty()) return true;
                    if (exp.getCompany() == null || exp.getCompany().getName() == null) return false;
                    return company.stream()
                            .map(String::toLowerCase)
                            .anyMatch(c -> exp.getCompany().getName().toLowerCase().contains(c));
                })

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
        model.addAttribute("companies", companyRepo.findAll());
        model.addAttribute("locations", locationRepo.findAll());

        List<Integer> years = IntStream.rangeClosed(2020, 2030).boxed().collect(Collectors.toList());
        model.addAttribute("years", years);

        // Pass filters back
        model.addAttribute("selectedYear", year);
        model.addAttribute("selectedName", name);
        model.addAttribute("selectedCompany", company);
        model.addAttribute("selectedRole", role);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedCtcMin", ctcMin);
        model.addAttribute("selectedCtcMax", ctcMax);
        model.addAttribute("selectedStipendMin", stipendMin);
        model.addAttribute("selectedStipendMax", stipendMax);
        return "experience-details";
    }

    @GetMapping("/experience-browser/{id}")
    public InternshipExperience getExperienceById(@PathVariable Long id) {
        return experienceRepository.findById(id).orElse(null);
    }
}