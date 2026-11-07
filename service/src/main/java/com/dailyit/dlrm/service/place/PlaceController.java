package com.dailyit.dlrm.service.place;

import com.dailyit.dlrm.core.config.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(summary = "장소 상세 조회", description = "운영 중(ACTIVE)인 장소만 조회할 수 있다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(
            responseCode = "400",
            description = "id가 숫자가 아님",
            content =
                    @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples =
                                    @ExampleObject(
                                            value =
                                                    """
                                                    {"status": 400, "code": "BAD_REQUEST", "message": "요청 내용이 올바르지 않습니다."}
                                                    """)))
    @ApiResponse(
            responseCode = "404",
            description = "장소가 없거나 운영 중(ACTIVE)이 아님",
            content =
                    @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples =
                                    @ExampleObject(
                                            value =
                                                    """
                                                    {"status": 404, "code": "PLACE_NOT_FOUND", "message": "장소를 찾을 수 없습니다."}
                                                    """)))
    @GetMapping("/{id}")
    public PlaceDetailResponse getPlaceDetail(
            @Parameter(description = "장소 ID", example = "1") @PathVariable Long id) {
        return placeService.getPlaceDetail(id);
    }

    @Operation(
            summary = "장소 목록 조회",
            description =
                    "기준점에서 반경 안에 있는 운영 중(ACTIVE)인 장소를 가까운 순으로 반환한다."
                            + " 기준점은 GPS 좌표(lat, lng)나 동네(neighborhood_id) 중 하나로 보낸다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(
            responseCode = "400",
            description =
                    "파라미터가 올바르지 않음 (기준점을 둘 다 보내거나 하나도 안 보냄, lat·lng 중 하나만 보냄,"
                            + " 좌표 범위 초과, 허용되지 않은 반경, 숫자가 아닌 값)",
            content =
                    @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples =
                                    @ExampleObject(
                                            value =
                                                    """
                                                    {"status": 400, "code": "BAD_REQUEST", "message": "요청 내용이 올바르지 않습니다."}
                                                    """)))
    @ApiResponse(
            responseCode = "404",
            description = "neighborhood_id에 해당하는 동네가 없음",
            content =
                    @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples =
                                    @ExampleObject(
                                            value =
                                                    """
                                                    {"status": 404, "code": "NEIGHBORHOOD_NOT_FOUND", "message": "동네를 찾을 수 없습니다."}
                                                    """)))
    @GetMapping
    public PlaceListResponse getPlaceList(@Valid @ParameterObject PlaceListQuery query) {
        return placeService.getPlaceList(query);
    }
}
