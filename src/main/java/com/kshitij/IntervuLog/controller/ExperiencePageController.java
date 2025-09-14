package com.kshitij.IntervuLog.controller;

import com.kshitij.IntervuLog.enums.ExperienceStatus;
import com.kshitij.IntervuLog.model.Company;
import com.kshitij.IntervuLog.model.InternshipExperience;
import com.kshitij.IntervuLog.model.Location;
import com.kshitij.IntervuLog.repository.CompanyRepository;
import com.kshitij.IntervuLog.repository.InternshipExperienceRepository;
import com.kshitij.IntervuLog.repository.LocationRepository;
import com.kshitij.IntervuLog.repository.UserRepository;
import com.kshitij.IntervuLog.spec.ExperienceSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.time.Year;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class ExperiencePageController {

    @Autowired
    private InternshipExperienceRepository internshipExperienceRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/experience-browser")
    public String showExperiencePage(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<String> company,
            @RequestParam(required = false) List<String> role,
            @RequestParam(required = false) List<String> type,
            @RequestParam(required = false) Integer ctcMin,
            @RequestParam(required = false) Integer ctcMax,
            @RequestParam(required = false) Integer stipendMin,
            @RequestParam(required = false) Integer stipendMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            Model model) {

        if (year == null) {
            year = Year.now().getValue() - 1;
        }
        Specification<InternshipExperience> spec = ExperienceSpecification.filterBy(
                year, name, company, role, type, ctcMin, ctcMax, stipendMin, stipendMax
        );

        Pageable pageable = PageRequest.of(page, size, Sort.by("company.name").ascending().and(Sort.by("fullName").ascending()));
        Page<InternshipExperience> pageResult = internshipExperienceRepository.findAll(spec, pageable);

        model.addAttribute("experiences", pageResult.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("size", size);

        List<Company> companies = companyRepository.findAll();
        companies.sort(Comparator.comparing(Company::getName));
        model.addAttribute("companies", companies);
        model.addAttribute("locations", locationRepository.findAll());

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
    @ResponseBody
    public InternshipExperience getExperienceById(@PathVariable Long id) {
        return internshipExperienceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Experience not found"));
    }

}