package com.dailyit.dlrm.core.exception;

public class PlaceNotFoundException extends BaseException {
    public PlaceNotFoundException() {
        super(PlaceErrorCode.PLACE_NOT_FOUND);
    }
}
