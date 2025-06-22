package com.kshitij.IntervuLog.repository;

import com.kshitij.IntervuLog.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Integer> {
    Location findByName(String name);
}
