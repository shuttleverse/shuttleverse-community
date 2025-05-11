package com.shuttleverse.community.repository;

import com.shuttleverse.community.model.OwnershipClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnershipClaimRepository extends JpaRepository<OwnershipClaim, Long> {

}