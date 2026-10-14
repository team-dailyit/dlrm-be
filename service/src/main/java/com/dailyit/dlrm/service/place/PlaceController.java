package com.dailyit.dlrm.service.place;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/places")
@RequiredArgsConstructor
public class PlaceController {
    private final PlaceService placeService;

    @GetMapping("/{id}")
    public PlaceDetailResponse getPlaceDetail(@PathVariable Long id) {
        return placeService.getPlaceDetail(id);
    }

    @GetMapping
    public PlaceListResponse getPlaceList(@Valid @ParameterObject PlaceListQuery query) {
        return placeService.getPlaceList(query);
    }
}
