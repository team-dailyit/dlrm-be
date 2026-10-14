package com.dailyit.dlrm.core.exception;

public class NeighborhoodNotFoundException extends BaseException {
    public NeighborhoodNotFoundException() {
        super(NeighborhoodErrorCode.NEIGHBORHOOD_NOT_FOUND);
    }
}
