package com.beehivemonitor.repository;

import com.beehivemonitor.entity.Sensor;
import com.beehivemonitor.entity.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SensorReadingRepository extends JpaRepository<SensorReading, UUID> {
    List<SensorReading> findBySensor(Sensor sensor);
    
    @Query("SELECT sr FROM SensorReading sr WHERE sr.sensor.hive.id = :hiveId " +
           "AND sr.id IN (SELECT MAX(sr2.id) FROM SensorReading sr2 WHERE sr2.sensor.hive.id = :hiveId " +
           "GROUP BY sr2.sensor.type)")
    List<SensorReading> findLatestReadingsByHiveId(@Param("hiveId") UUID hiveId);
    
    List<SensorReading> findBySensorId(UUID sensorId);
    
    /**
     * Delete all sensor readings older than the specified timestamp
     */
    @Modifying
    @Query("DELETE FROM SensorReading sr WHERE sr.timestamp < :cutoffDate")
    void deleteByTimestampBefore(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Count sensor readings older than the specified timestamp
     */
    @Query("SELECT COUNT(sr) FROM SensorReading sr WHERE sr.timestamp < :cutoffDate")
    long countByTimestampBefore(@Param("cutoffDate") LocalDateTime cutoffDate);
}


