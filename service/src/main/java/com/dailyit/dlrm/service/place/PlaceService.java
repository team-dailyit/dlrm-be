package com.dailyit.dlrm.service.place;

import com.dailyit.dlrm.core.exception.PlaceNotFoundException;
import com.dailyit.dlrm.core.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {
    private final PlaceRepository placeRepository;

    public PlaceDetailResponse getPlaceDetail(Long id) {
        return placeRepository
                .findActiveById(id)
                .map(PlaceDetailResponse::from)
                .orElseThrow(PlaceNotFoundException::new);
    }
}
