package com.springboot.stores.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationCheckDto {
    @NotNull(message = "Reservation time is required")
    private LocalDateTime reservationTime;
}
