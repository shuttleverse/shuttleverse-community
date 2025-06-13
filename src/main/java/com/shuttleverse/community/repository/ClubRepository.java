package com.shuttleverse.community.repository;

import com.shuttleverse.community.model.Club;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ClubRepository extends JpaRepository<Club, UUID>, JpaSpecificationExecutor<Club> {

}