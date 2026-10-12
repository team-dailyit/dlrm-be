package com.dailyit.dlrm.service.neighborhood;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/neighborhoods")
@RequiredArgsConstructor
public class NeighborhoodController {
    private final NeighborhoodService neighborhoodService;

    @GetMapping
    public List<NeighborhoodResponse> getNeighborhoods() {
        return neighborhoodService.getNeighborhoods();
    }
}
