package com.shuttleverse.community.repository;

import com.shuttleverse.community.model.SVVerificationFile;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SVVerificationFileRepository extends JpaRepository<SVVerificationFile, UUID> {

}
