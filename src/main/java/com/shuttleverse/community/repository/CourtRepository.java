package com.shuttleverse.community.repository;

import com.shuttleverse.community.model.Court;
import java.util.UUID;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CourtRepository extends JpaRepository<Court, UUID>,
    JpaSpecificationExecutor<Court> {

  @Query(value = """
      SELECT c.* FROM public.court c
      WHERE ST_Within(
            ST_SetSRID(c.location_point, 4326),
            ST_SetSRID(ST_MakeEnvelope(:minLon, :minLat, :maxLon, :maxLat), 4326)
      )
      """,
      countQuery = """
          SELECT COUNT(*) FROM public.court
          WHERE ST_Within(
            ST_SetSRID(location_point, 4326),
            ST_SetSRID(ST_MakeEnvelope(:minLon, :minLat, :maxLon, :maxLat), 4326)
          )
          """,
      nativeQuery = true)
  Page<Court> findWithinBounds(
      @Param("minLon") double minLon,
      @Param("minLat") double minLat,
      @Param("maxLon") double maxLon,
      @Param("maxLat") double maxLat,
      Pageable pageable);

  /**
   * Returns a paginated list of Courts within the given distance at a given location.
   *
   * @param location the location to query for
   * @param distance the distance radius in meters
   * @return the list of courts within distance of the given location
   **/
  @Query(value = """
      SELECT c.* FROM public.court c
      WHERE ST_DWithin(
        c.location_point,
        :location,
        :distance
      )
      """,
      countQuery = """
          SELECT COUNT(*) FROM public.court
          WHERE ST_DWithin(
              location_point,
              :location,
              :distance
          )
          """,
      nativeQuery = true)
  Page<Court> findWithinDistance(
      @Param("location") Point location,
      @Param("distance") int distance,
      Pageable pageable);
}