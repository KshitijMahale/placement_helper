package com.kshitij.placement_helper.repository;

import com.kshitij.placement_helper.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Integer> {
    Company findByName(String name);
}
