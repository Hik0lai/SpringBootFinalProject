package com.beehivemonitor.repository;

import com.beehivemonitor.entity.Hive;
import com.beehivemonitor.entity.Inspection;
import com.beehivemonitor.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspectionRepository extends JpaRepository<Inspection, Long> {
    List<Inspection> findByHive(Hive hive);
    List<Inspection> findByHiveId(Long hiveId);
    
    @Query("SELECT i FROM Inspection i WHERE i.hive.user.id = :userId")
    List<Inspection> findByUserId(@Param("userId") Long userId);
}


