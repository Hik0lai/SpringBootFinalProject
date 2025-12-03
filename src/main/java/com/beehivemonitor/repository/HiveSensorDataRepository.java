package com.beehivemonitor.repository;

import com.beehivemonitor.entity.HiveSensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HiveSensorDataRepository extends JpaRepository<HiveSensorData, Long> {
    
    List<HiveSensorData> findByHiveIdOrderByTimestampAsc(Long hiveId);
    
    @Query("SELECT hsd FROM HiveSensorData hsd WHERE hsd.hive.id = :hiveId " +
           "AND hsd.timestamp >= :startDate AND hsd.timestamp <= :endDate " +
           "ORDER BY hsd.timestamp ASC")
    List<HiveSensorData> findByHiveIdAndTimestampBetween(
        @Param("hiveId") Long hiveId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}


