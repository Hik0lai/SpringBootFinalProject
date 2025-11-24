package com.beehivemonitor.repository;

import com.beehivemonitor.entity.Hive;
import com.beehivemonitor.entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {
    List<Sensor> findByHive(Hive hive);
    List<Sensor> findByHiveId(Long hiveId);
}


