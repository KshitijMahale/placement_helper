package com.kshitij.placement_helper.controller;

import com.kshitij.placement_helper.enums.ExperienceStatus;
import com.kshitij.placement_helper.model.InternshipExperience;
import com.kshitij.placement_helper.repository.InternshipExperienceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/experiences")
@PreAuthorize("hasRole('ADMIN')")
public class AdminExperienceController {

    @Autowired
    private InternshipExperienceRepository experienceRepository;

    @GetMapping("/pending")
    public String showPendingExperiences(Model model) {
        List<InternshipExperience> pendingExps = experienceRepository.findByStatus(ExperienceStatus.PENDING);
        model.addAttribute("pendingExps", pendingExps);
        return "admin/pending_experiences";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<InternshipExperience> getExperienceDetails(@PathVariable Long id) {
        return experienceRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/approve")
    public String approveExperience(@PathVariable Long id) {
        experienceRepository.findById(id).ifPresent(exp -> {
            exp.setStatus(ExperienceStatus.APPROVED);
            experienceRepository.save(exp);
        });
        return "redirect:/admin/experiences/pending";
    }

    @PostMapping("/{id}/delete")
    public String deleteExperience(@PathVariable Long id) {
        experienceRepository.deleteById(id);
        return "redirect:/admin/experiences/pending";
    }
}
