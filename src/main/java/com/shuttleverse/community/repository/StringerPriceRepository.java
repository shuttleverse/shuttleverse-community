package com.shuttleverse.community.repository;

import com.shuttleverse.community.model.StringerPrice;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StringerPriceRepository extends JpaRepository<StringerPrice, UUID> {

}