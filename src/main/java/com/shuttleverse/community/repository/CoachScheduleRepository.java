package com.shuttleverse.community.repository;

import com.shuttleverse.community.model.CoachSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoachScheduleRepository extends JpaRepository<CoachSchedule, Long> {

}