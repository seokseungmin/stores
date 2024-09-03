package com.springboot.stores.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ReservationStatus {
    PENDING("예약 대기"),
    APPROVED("예약 승인"),  // 추가
    REJECTED("예약 거절"),  // 추가
    CHECKED_IN("예약 체크인"),
    FAILED("예약 체크인 실패"),
    COMPLETED("예약 완료");

    private final String description;

}
