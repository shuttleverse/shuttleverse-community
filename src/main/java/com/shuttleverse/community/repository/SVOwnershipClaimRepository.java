package com.shuttleverse.community.repository;

import com.shuttleverse.community.model.SVOwnershipClaim;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SVOwnershipClaimRepository extends JpaRepository<SVOwnershipClaim, UUID>,
    QuerydslPredicateExecutor<SVOwnershipClaim> {

  SVOwnershipClaim findByCreatorId(UUID id);

  List<SVOwnershipClaim> findAllByCreatorIdOrderByCreatedAtDesc(UUID userId);

  @Query("SELECT DISTINCT c FROM SVOwnershipClaim c " +
      "LEFT JOIN FETCH c.creator " +
      "LEFT JOIN FETCH c.files " +
      "ORDER BY c.createdAt DESC")
  Page<SVOwnershipClaim> findAllWithCreatorAndFiles(Pageable pageable);

  @Query("SELECT DISTINCT c FROM SVOwnershipClaim c " +
      "LEFT JOIN FETCH c.creator " +
      "LEFT JOIN FETCH c.files " +
      "WHERE c.creator.id = :userId " +
      "ORDER BY c.createdAt DESC")
  List<SVOwnershipClaim> findAllByCreatorIdWithFilesOrderByCreatedAtDesc(UUID userId);

  @Query("SELECT DISTINCT c FROM SVOwnershipClaim c " +
      "LEFT JOIN FETCH c.creator " +
      "LEFT JOIN FETCH c.files " +
      "WHERE c.id = :claimId")
  SVOwnershipClaim findByIdWithCreatorAndFiles(UUID claimId);
}