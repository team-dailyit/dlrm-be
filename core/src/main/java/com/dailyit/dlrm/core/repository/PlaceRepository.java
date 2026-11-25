package com.dailyit.dlrm.core.repository;

import com.dailyit.dlrm.core.domain.Place;
import com.dailyit.dlrm.core.domain.type.PlaceStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    Optional<Place> findByIdAndStatus(Long id, PlaceStatus status);

    default Optional<Place> findActiveById(Long id) {
        return findByIdAndStatus(id, PlaceStatus.ACTIVE);
    }

    @EntityGraph(attributePaths = "operatingHours")
    List<Place> findAllByStatus(PlaceStatus status);

    default List<Place> findAllActive() {
        return findAllByStatus(PlaceStatus.ACTIVE);
    }

    // 반경 조건은 GiST 인덱스를 쓰도록 ST_DWithin으로 걸고, 정렬은 반올림 전 거리로 한다.
    @Query(
            value =
                    """
        WITH center AS (
            SELECT ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography AS point
        )
        SELECT p.id, ROUND(ST_Distance(p.location, c.point)) AS distanceM
        FROM places p, center c
        WHERE p.status = 'ACTIVE'
          AND ST_DWithin(p.location, c.point, :radius)
        ORDER BY ST_Distance(p.location, c.point), p.id
        """,
            nativeQuery = true)
    List<PlaceDistance> findActiveWithinRadius(
            @Param("lat") double lat, @Param("lng") double lng, @Param("radius") double radius);

    @EntityGraph(attributePaths = "operatingHours")
    List<Place> findAllWithOperatingHoursByIdIn(List<Long> ids);
}
