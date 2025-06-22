package com.kshitij.IntervuLog.repository;

import com.kshitij.IntervuLog.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CompanyRepository extends JpaRepository<Company, Integer> {
    Company findByName(String name);
    @Query("SELECT COUNT(c) FROM Company c")
    long countAllCompanies();
}
