package com.dailyit.dlrm.service.neighborhood;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "동네 목록 조회", description = "전체 동네를 ID 오름차순으로 반환한다. 영업 중인 장소가 없는 동네도 포함한다.")
    @GetMapping
    public List<NeighborhoodResponse> getNeighborhoods() {
        return neighborhoodService.getNeighborhoods();
    }
}
