package com.beehivemonitor.repository;

import com.beehivemonitor.entity.Alert;
import com.beehivemonitor.entity.Hive;
import com.beehivemonitor.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByHive(Hive hive);
    List<Alert> findByHiveId(Long hiveId);
    
    @Query("SELECT a FROM Alert a WHERE a.hive.user.id = :userId")
    List<Alert> findByUserId(@Param("userId") Long userId);
}


