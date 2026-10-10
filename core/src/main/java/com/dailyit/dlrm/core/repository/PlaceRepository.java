package com.dailyit.dlrm.core.repository;

import com.dailyit.dlrm.core.domain.Place;
import com.dailyit.dlrm.core.domain.type.PlaceStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    Optional<Place> findByIdAndStatus(Long id, PlaceStatus status);

    default Optional<Place> findActiveById(Long id) {
        return findByIdAndStatus(id, PlaceStatus.ACTIVE);
    }
}
