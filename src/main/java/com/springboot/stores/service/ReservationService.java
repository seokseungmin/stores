package com.springboot.stores.service;

import com.springboot.stores.common.model.ServiceResult;
import com.springboot.stores.dto.ReservationDto;

import java.time.LocalDateTime;


public interface ReservationService {
    ServiceResult makeReservation(ReservationDto reservationDto, String token);

    ServiceResult confirmArrival(String token, LocalDateTime requestedReservationTime);

    ServiceResult approveReservation(Long reservationId, String token);

    ServiceResult rejectReservation(Long reservationId, String token);

}
