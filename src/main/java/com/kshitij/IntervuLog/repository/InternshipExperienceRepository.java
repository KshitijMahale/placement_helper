package com.kshitij.IntervuLog.repository;

import com.kshitij.IntervuLog.enums.ExperienceStatus;
import com.kshitij.IntervuLog.model.InternshipExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import com.kshitij.IntervuLog.model.User;
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
