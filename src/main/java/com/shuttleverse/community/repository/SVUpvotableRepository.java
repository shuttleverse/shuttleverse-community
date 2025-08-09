package com.shuttleverse.community.repository;

import com.shuttleverse.community.constants.SVEntityType;
import com.shuttleverse.community.constants.SVInfoType;
import com.shuttleverse.community.model.SVBaseUpvotable;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface SVUpvotableRepository<T extends SVBaseUpvotable> extends JpaRepository<T, UUID>,
    QuerydslPredicateExecutor<T> {

  default SVEntityType getEntityType() {
    return null;
  }

  default SVInfoType getInfoType() {
    return null;
  }
}
