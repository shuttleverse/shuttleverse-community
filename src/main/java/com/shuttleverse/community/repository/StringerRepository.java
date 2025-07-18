package com.shuttleverse.community.repository;

import com.shuttleverse.community.model.Stringer;
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
public interface StringerRepository extends JpaRepository<Stringer, UUID>,
    JpaSpecificationExecutor<Stringer> {

  @Query(value = """
      SELECT s.* FROM public.stringer s
      WHERE ST_Within(
            ST_SetSRID(location_point, 4326),
            ST_SetSRID(ST_MakeEnvelope(:minLon, :minLat, :maxLon, :maxLat), 4326)
      )
      """,
      countQuery = """
          SELECT COUNT(*) FROM public.stringer
          WHERE ST_Within(
            ST_SetSRID(location_point, 4326),
            ST_SetSRID(ST_MakeEnvelope(:minLon, :minLat, :maxLon, :maxLat), 4326)
          )
          """,
      nativeQuery = true)
  Page<Stringer> findWithinBounds(
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
   * @return the list of stringers within distance of the given location
   **/
  @Query(value = """ 
      SELECT s.* FROM public.court s
      WHERE ST_DWithin(
        s.location_point,
        :location,
        :distance
      )
      """,
      countQuery = """
          SELECT COUNT(*) FROM public.stringer
          WHERE ST_DWithin(
              location_point,
              :location,
              :distance
          )
          """,
      nativeQuery = true)
  Page<Stringer> findWithinDistance(
      @Param("location") Point location,
      @Param("distance") int distance,
      Pageable pageable);
}