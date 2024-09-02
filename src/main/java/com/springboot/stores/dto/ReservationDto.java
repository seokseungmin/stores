package com.springboot.stores.dto;


import com.springboot.stores.entity.Store;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationDto {
    @NotNull(message = "Store name is required")
    private String storeName;

    @NotNull(message = "Reservation time is required")
    private LocalDateTime reservationTime;
}
