package com.shuttleverse.community.repository;

import com.shuttleverse.community.model.CoachPrice;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoachPriceRepository extends JpaRepository<CoachPrice, UUID> {

}
