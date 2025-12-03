package com.beehivemonitor.repository;

import com.beehivemonitor.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlertRepository extends JpaRepository<Alert, UUID> {
    @Query("SELECT a FROM Alert a WHERE a.hive.user.id = :userId")
    List<Alert> findByUserId(@Param("userId") UUID userId);
}


