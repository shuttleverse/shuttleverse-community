package com.shuttleverse.community.repository;

import com.shuttleverse.community.model.Court;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CourtRepository extends JpaRepository<Court, UUID>, JpaSpecificationExecutor<Court> {

}