package com.kshitij.IntervuLog.controller;

import com.kshitij.IntervuLog.repository.CompanyRepository;
import com.kshitij.IntervuLog.repository.InternshipExperienceRepository;
import com.kshitij.IntervuLog.repository.LocationRepository;
import com.kshitij.IntervuLog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private InternshipExperienceRepository internshipExperienceRepository;

    @GetMapping("/dashboard")
    public String showAdminDashboard(Model model) {
        long totalCompanies = companyRepository.count();
        long totalExperiences = internshipExperienceRepository.count();
        long totalUsers = userRepository.count();

        model.addAttribute("totalCompanies", totalCompanies);
        model.addAttribute("totalExperiences", totalExperiences);
        model.addAttribute("totalUsers", totalUsers);
        return "admin/admin-dashboard";
    }
}

