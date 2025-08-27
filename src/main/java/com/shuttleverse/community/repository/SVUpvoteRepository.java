package com.shuttleverse.community.repository;

import com.shuttleverse.community.model.SVUpvote;
import com.shuttleverse.community.model.SVUser;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface SVUpvoteRepository extends JpaRepository<SVUpvote, UUID>,
    QuerydslPredicateExecutor<SVUpvote> {

  Optional<SVUpvote> findByUpvoteCreatorAndEntityId(SVUser upvoteCreator, UUID entityId);

  void deleteByEntityId(UUID entityId);
}
