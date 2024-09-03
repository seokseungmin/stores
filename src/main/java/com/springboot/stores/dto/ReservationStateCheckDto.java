package com.springboot.stores.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationStateCheckDto {
    @NotNull(message = "Reservation time is required")
    private Long reservationId;  // 예약 ID 추가
    @NotNull(message = "Reservation time is required")
    private LocalDateTime reservationTime;
    private String storeName;
    private String reservationState;
    private String username;
}
