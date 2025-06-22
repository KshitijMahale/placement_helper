package com.kshitij.IntervuLog.model;


import com.kshitij.IntervuLog.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
@Table(name = "user_details")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    private String name;
    @Column(unique = true)
    private String email;

    private String degree;
    private String academicYear;
    private String department;
    private String passoutYear;
    private String firstName;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private UserRole userRole = UserRole.STUDENT;
}