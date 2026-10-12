package com.dailyit.dlrm.core.repository;

import com.dailyit.dlrm.core.domain.Neighborhood;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NeighborhoodRepository extends JpaRepository<Neighborhood, Long> {
    List<Neighborhood> findAllByOrderByIdAsc();
}
