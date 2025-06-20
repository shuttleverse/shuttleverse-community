package com.shuttleverse.community.repository;

import com.shuttleverse.community.model.Upvote;
import com.shuttleverse.community.model.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UpvoteRepository extends JpaRepository<Upvote, UUID>,
    JpaSpecificationExecutor<Upvote> {

  Optional<Upvote> findByUpvoteCreatorAndEntityId(User upvoteCreator, UUID entityId);
}
