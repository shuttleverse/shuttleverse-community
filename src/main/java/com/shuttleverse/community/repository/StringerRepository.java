package com.shuttleverse.community.repository;

import com.shuttleverse.community.model.Stringer;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StringerRepository extends JpaRepository<Stringer, UUID>, JpaSpecificationExecutor<Stringer> {

}