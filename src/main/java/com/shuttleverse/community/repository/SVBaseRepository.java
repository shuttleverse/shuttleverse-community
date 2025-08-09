package com.shuttleverse.community.repository;

import com.shuttleverse.community.model.SVBaseModel;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface SVBaseRepository<T extends SVBaseModel> extends
    JpaRepository<T, UUID>,
    QuerydslPredicateExecutor<T> {

}
