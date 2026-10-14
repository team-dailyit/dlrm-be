package com.dailyit.dlrm.service.place;

import com.dailyit.dlrm.core.domain.Neighborhood;
import com.dailyit.dlrm.core.domain.Place;
import com.dailyit.dlrm.core.exception.NeighborhoodNotFoundException;
import com.dailyit.dlrm.core.exception.PlaceNotFoundException;
import com.dailyit.dlrm.core.repository.NeighborhoodRepository;
import com.dailyit.dlrm.core.repository.PlaceDistance;
import com.dailyit.dlrm.core.repository.PlaceRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final NeighborhoodRepository neighborhoodRepository;
    private final Clock clock;

    public PlaceDetailResponse getPlaceDetail(Long id) {
        LocalDateTime now = LocalDateTime.now(clock);

        return placeRepository
                .findActiveById(id)
                .map(place -> PlaceDetailResponse.from(place, now))
                .orElseThrow(PlaceNotFoundException::new);
    }

    public PlaceListResponse getPlaceList(PlaceListQuery query) {
        LocalDateTime now = LocalDateTime.now(clock);
        double lat;
        double lng;

        if (query.neighborhoodId() != null) {
            Neighborhood neighborhood =
                    neighborhoodRepository
                            .findById(query.neighborhoodId())
                            .orElseThrow(NeighborhoodNotFoundException::new);
            lat = neighborhood.getLat().doubleValue();
            lng = neighborhood.getLng().doubleValue();
        } else {
            lat = query.lat();
            lng = query.lng();
        }

        List<PlaceDistance> distances =
                placeRepository.findActiveWithinRadius(lat, lng, query.radius());
        List<Long> ids = distances.stream().map(PlaceDistance::getId).toList();
        List<Place> places = placeRepository.findAllWithOperatingHoursByIdIn(ids);

        // IN 조회는 순서를 보장하지 않으므로, 거리순 결과(distances) 순서대로 다시 맞춘다.
        Map<Long, Place> placeById =
                places.stream().collect(Collectors.toMap(Place::getId, Function.identity()));
        List<PlaceListItemResponse> items = new ArrayList<>();

        for (PlaceDistance distance : distances) {
            Place place = placeById.get(distance.getId());
            items.add(PlaceListItemResponse.from(place, distance.getDistanceM(), now));
        }

        return new PlaceListResponse(items);
    }
}
