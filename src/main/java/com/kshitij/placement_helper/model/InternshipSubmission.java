package com.kshitij.placement_helper.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "internship_submissions")
public class InternshipSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String course;
    private String company;
    private String otherCompany;
    private String jobProfile;
    private String otherJobProfile;
    private String offerType;
    private Integer internshipStipend;
    private Integer ctc;
    private String location;
    private LocalDate processDate;
    private String linkedin;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String rounds; // store JSON string of rounds

    @Column(updatable = false)
    private LocalDateTime submissionTime = LocalDateTime.now();

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getOtherCompany() {
        return otherCompany;
    }

    public void setOtherCompany(String otherCompany) {
        this.otherCompany = otherCompany;
    }

    public String getJobProfile() {
        return jobProfile;
    }

    public void setJobProfile(String jobProfile) {
        this.jobProfile = jobProfile;
    }

    public String getOtherJobProfile() {
        return otherJobProfile;
    }

    public void setOtherJobProfile(String otherJobProfile) {
        this.otherJobProfile = otherJobProfile;
    }

    public String getOfferType() {
        return offerType;
    }

    public void setOfferType(String offerType) {
        this.offerType = offerType;
    }

    public Integer getInternshipStipend() {
        return internshipStipend;
    }

    public void setInternshipStipend(Integer internshipStipend) {
        this.internshipStipend = internshipStipend;
    }

    public Integer getCtc() {
        return ctc;
    }

    public void setCtc(Integer ctc) {
        this.ctc = ctc;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getProcessDate() {
        return processDate;
    }

    public void setProcessDate(LocalDate processDate) {
        this.processDate = processDate;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getRounds() {
        return rounds;
    }

    public void setRounds(String rounds) {
        this.rounds = rounds;
    }

    public LocalDateTime getSubmissionTime() {
        return submissionTime;
    }

    public void setSubmissionTime(LocalDateTime submissionTime) {
        this.submissionTime = submissionTime;
    }
} 