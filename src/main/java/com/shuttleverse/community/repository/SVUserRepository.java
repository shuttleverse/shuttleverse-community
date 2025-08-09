package com.shuttleverse.community.repository;

import com.shuttleverse.community.model.SVUser;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SVUserRepository extends JpaRepository<SVUser, UUID>,
    QuerydslPredicateExecutor<SVUser> {

  SVUser findByEmail(String email);
}