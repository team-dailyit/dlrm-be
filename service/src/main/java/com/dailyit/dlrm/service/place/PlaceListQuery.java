package com.dailyit.dlrm.service.place;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.util.Set;
import org.springframework.web.bind.annotation.BindParam;

public record PlaceListQuery(
        @Parameter(
                        description =
                                "기준점 GPS 위도(-90~90). lng와 함께 보내고, neighborhood_id와 함께 보낼 수 없다",
                        example = "37.5443210")
                @BindParam("lat")
                @DecimalMin("-90")
                @DecimalMax("90")
                Double lat,
        @Parameter(
                        description =
                                "기준점 GPS 경도(-180~180). lat과 함께 보내고, neighborhood_id와 함께 보낼 수 없다",
                        example = "127.0557650")
                @BindParam("lng")
                @DecimalMin("-180")
                @DecimalMax("180")
                Double lng,
        @Parameter(
                        description = "기준점으로 쓸 동네 ID. 동네의 역 좌표를 기준점으로 쓰고, lat·lng와 함께 보낼 수 없다",
                        example = "1")
                @BindParam("neighborhood_id")
                Long neighborhoodId,
        @Parameter(
                        description = "반경(m). 300, 500, 1000, 2000 중 하나",
                        example = "500",
                        schema =
                                @Schema(
                                        type = "integer",
                                        allowableValues = {"300", "500", "1000", "2000"},
                                        defaultValue = "500"))
                @BindParam("radius")
                Integer radius) {

    private static final int DEFAULT_RADIUS = 500;
    private static final Set<Integer> ALLOWED_RADII = Set.of(300, 500, 1000, 2000);

    public PlaceListQuery {
        if (radius == null) {
            radius = DEFAULT_RADIUS;
        }
    }

    @AssertTrue
    public boolean isEitherLocationOrNeighborhood() {
        boolean hasLocation = lat != null || lng != null;
        boolean hasNeighborhood = neighborhoodId != null;
        return hasLocation != hasNeighborhood;
    }

    @AssertTrue
    public boolean isLatLngPaired() {
        return (lat == null) == (lng == null);
    }

    @AssertTrue
    public boolean isAllowedRadius() {
        return ALLOWED_RADII.contains(radius);
    }
}
