package com.kshitij.placement_helper.model;

import com.kshitij.placement_helper.enums.ExperienceStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "internship_experiences")
public class InternshipExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String course;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    private String otherCompany;
    private String jobProfile;
    private String otherJobProfile;
    private String offerType;
    private Integer internshipStipend;
    private Integer ctc;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    private LocalDate processDate;
    private String linkedin;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String rounds;

    private LocalDateTime submissionTime;

    @ManyToOne
    @JoinColumn(name = "submitted_by", nullable = false)
    private User submittedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExperienceStatus status = ExperienceStatus.PENDING;

    @PrePersist
    protected void onCreate() {
        if (submissionTime == null) {
            submissionTime = LocalDateTime.now();
        }
    }

}
