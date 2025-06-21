package com.kshitij.placement_helper.repository;

import com.kshitij.placement_helper.enums.ExperienceStatus;
import com.kshitij.placement_helper.model.InternshipExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import com.kshitij.placement_helper.model.User;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InternshipExperienceRepository extends JpaRepository<InternshipExperience, Long> {
    List<InternshipExperience> findByStatus(ExperienceStatus status);
    List<InternshipExperience> findBySubmittedBy(User submittedBy);
    @Query("SELECT COUNT(e) FROM InternshipExperience e")
    long countAllExperiences();

//    @Query("SELECT SUM(e.viewCount) FROM InternshipExperience e")
//    long sumViews();

}
