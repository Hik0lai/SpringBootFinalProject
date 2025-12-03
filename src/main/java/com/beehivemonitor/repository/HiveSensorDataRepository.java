package com.beehivemonitor.repository;

import com.beehivemonitor.entity.HiveSensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface HiveSensorDataRepository extends JpaRepository<HiveSensorData, UUID> {
    
    List<HiveSensorData> findByHiveIdOrderByTimestampAsc(UUID hiveId);
    
    @Query("SELECT hsd FROM HiveSensorData hsd WHERE hsd.hive.id = :hiveId " +
           "AND hsd.timestamp >= :startDate AND hsd.timestamp <= :endDate " +
           "ORDER BY hsd.timestamp ASC")
    List<HiveSensorData> findByHiveIdAndTimestampBetween(
        @Param("hiveId") UUID hiveId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}


