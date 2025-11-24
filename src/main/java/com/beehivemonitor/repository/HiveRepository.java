package com.beehivemonitor.repository;

import com.beehivemonitor.entity.Hive;
import com.beehivemonitor.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HiveRepository extends JpaRepository<Hive, Long> {
    List<Hive> findByUser(User user);
    List<Hive> findByUserId(Long userId);
}


