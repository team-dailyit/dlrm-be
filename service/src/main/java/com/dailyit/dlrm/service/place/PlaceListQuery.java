package com.dailyit.dlrm.service.place;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.util.Set;
import org.springframework.web.bind.annotation.BindParam;

public record PlaceListQuery(
        @BindParam("lat") @DecimalMin("-90") @DecimalMax("90") Double lat,
        @BindParam("lng") @DecimalMin("-180") @DecimalMax("180") Double lng,
        @BindParam("neighborhood_id") Long neighborhoodId,
        @BindParam("radius") Integer radius) {

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
