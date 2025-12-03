package com.beehivemonitor.repository;

import com.beehivemonitor.entity.Inspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InspectionRepository extends JpaRepository<Inspection, UUID> {
    @Query("SELECT i FROM Inspection i WHERE i.hive.user.id = :userId")
    List<Inspection> findByUserId(@Param("userId") UUID userId);
}


