package com.springboot.stores.entity;

import lombok.*;

@RequiredArgsConstructor
public enum ReservationStatus {
    PENDING("예약 대기"),
    CHECKED_IN("예약 체크인"),
    FAILED("예약 체크인 실패"),
    COMPLETED("예약 완료");

    private final String description;

}
