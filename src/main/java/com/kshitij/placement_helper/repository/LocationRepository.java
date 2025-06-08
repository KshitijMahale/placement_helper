package com.kshitij.placement_helper.repository;

import com.kshitij.placement_helper.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Integer> {
    Location findByName(String name);
}
