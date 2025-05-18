package com.kshitij.placement_helper.repository;

import com.kshitij.placement_helper.model.InternshipSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InternshipSubmissionRepository extends JpaRepository<InternshipSubmission, Long> {
} 