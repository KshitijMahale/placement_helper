package com.kshitij.IntervuLog.controller;

import com.kshitij.IntervuLog.enums.ExperienceStatus;
import com.kshitij.IntervuLog.model.InternshipExperience;
import com.kshitij.IntervuLog.repository.InternshipExperienceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/experiences")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
public class AdminExperienceController {

    @Autowired
    private InternshipExperienceRepository internshipExperienceRepository;

    @GetMapping("/pending")
    public String showPendingExperiences(Model model) {
        List<InternshipExperience> pendingExps = internshipExperienceRepository.findByStatus(ExperienceStatus.PENDING);
        model.addAttribute("pendingExps", pendingExps);
        return "admin/pending_experiences";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<InternshipExperience> getExperienceDetails(@PathVariable Long id) {
        return internshipExperienceRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/approve")
    public String approveExperience(@PathVariable Long id) {
        internshipExperienceRepository.findById(id).ifPresent(exp -> {
            exp.setStatus(ExperienceStatus.APPROVED);
            internshipExperienceRepository.save(exp);
        });
        return "redirect:/admin/experiences/pending";
    }

    @PostMapping("/{id}/delete")
    public String deleteExperience(@PathVariable Long id) {
        internshipExperienceRepository.deleteById(id);
        return "redirect:/admin/experiences/pending";
    }
}
