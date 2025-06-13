package com.shuttleverse.community.repository;

import com.shuttleverse.community.model.CourtSchedule;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourtScheduleRepository extends JpaRepository<CourtSchedule, UUID> {

}