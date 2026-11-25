package com.dailyit.dlrm.service.neighborhood;

import com.dailyit.dlrm.core.repository.NeighborhoodRepository;
import com.dailyit.dlrm.core.repository.PlaceRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NeighborhoodService {
    private final NeighborhoodRepository neighborhoodRepository;
    private final PlaceRepository placeRepository;
    private final Clock clock;

    public List<NeighborhoodResponse> getNeighborhoods() {
        LocalDateTime now = LocalDateTime.now(clock);

        Map<Long, Long> openPlaceCounts =
                placeRepository.findAllActive().stream()
                        .filter(place -> place.isOpenAt(now))
                        .collect(
                                Collectors.groupingBy(
                                        place -> place.getNeighborhood().getId(),
                                        Collectors.counting()));

        return neighborhoodRepository.findAllByOrderByIdAsc().stream()
                .map(
                        neighborhood ->
                                NeighborhoodResponse.from(
                                        neighborhood,
                                        openPlaceCounts.getOrDefault(neighborhood.getId(), 0L)))
                .toList();
    }
}
