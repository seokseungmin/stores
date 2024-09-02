package com.springboot.stores.dto;


import com.springboot.stores.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationStateCheckDto {
    @NotNull(message = "Reservation time is required")
    private LocalDateTime reservationTime;
    private String storeName;
    private String reservationState;
    private String username;
}
