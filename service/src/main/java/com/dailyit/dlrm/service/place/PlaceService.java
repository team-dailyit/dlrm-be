package com.dailyit.dlrm.service.place;

import com.dailyit.dlrm.core.exception.PlaceNotFoundException;
import com.dailyit.dlrm.core.repository.PlaceRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final Clock clock;

    public PlaceDetailResponse getPlaceDetail(Long id) {
        LocalDateTime now = LocalDateTime.now(clock);

        return placeRepository
                .findActiveById(id)
                .map(place -> PlaceDetailResponse.from(place, now))
                .orElseThrow(PlaceNotFoundException::new);
    }
}
