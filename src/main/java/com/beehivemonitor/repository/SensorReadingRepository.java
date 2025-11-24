package com.beehivemonitor.repository;

import com.beehivemonitor.entity.Sensor;
import com.beehivemonitor.entity.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {
    List<SensorReading> findBySensor(Sensor sensor);
    
    @Query("SELECT sr FROM SensorReading sr WHERE sr.sensor.hive.id = :hiveId " +
           "AND sr.id IN (SELECT MAX(sr2.id) FROM SensorReading sr2 WHERE sr2.sensor.hive.id = :hiveId " +
           "GROUP BY sr2.sensor.type)")
    List<SensorReading> findLatestReadingsByHiveId(@Param("hiveId") Long hiveId);
    
    List<SensorReading> findBySensorId(Long sensorId);
}


