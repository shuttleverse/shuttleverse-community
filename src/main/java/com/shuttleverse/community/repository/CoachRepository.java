package com.shuttleverse.community.repository;

import com.shuttleverse.community.model.Coach;
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
public interface CoachRepository extends JpaRepository<Coach, UUID>,
    JpaSpecificationExecutor<Coach> {

  @Query(value = """
      SELECT c.* FROM public.coach c
      WHERE ST_Within(
            ST_SetSRID(location_point, 4326),
            ST_SetSRID(ST_MakeEnvelope(:minLon, :minLat, :maxLon, :maxLat), 4326)
      )
      """,
      countQuery = """
          SELECT COUNT(*) FROM public.coach
          WHERE ST_Within(
            ST_SetSRID(location_point, 4326),
            ST_SetSRID(ST_MakeEnvelope(:minLon, :minLat, :maxLon, :maxLat), 4326)
          )
          """,
      nativeQuery = true)
  Page<Coach> findWithinBounds(
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
   * @return the list of coaches within distance of the location
   **/
  @Query(value = """
      SELECT c.* FROM public.coach c
      WHERE ST_DWithin(
        c.location_point,
        :location,
        :distance
      )
      """,
      countQuery = """
          SELECT COUNT(*) FROM public.coach
          WHERE ST_DWithin(
              location_point,
              :location,
              :distance
          )
          """,
      nativeQuery = true)
  Page<Coach> findWithinDistance(
      @Param("location") Point location,
      @Param("distance") int distance,
      Pageable pageable);
}