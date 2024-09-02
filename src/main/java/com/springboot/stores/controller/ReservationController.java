package com.springboot.stores.controller;

import com.springboot.stores.common.model.ResponseError;
import com.springboot.stores.common.model.ResponseResult;
import com.springboot.stores.common.model.ServiceResult;
import com.springboot.stores.dto.ReservationCheckDto;
import com.springboot.stores.dto.ReservationDto;
import com.springboot.stores.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    // API 매장 예약
    @PostMapping
    public ResponseEntity<?> makeReservation(@RequestBody @Valid ReservationDto reservationDto, @RequestHeader("STORE-TOKEN") String token, Errors errors) {
        List<ResponseError> responseErrorList = new ArrayList<>();

        if (errors.hasErrors()) {
            errors.getAllErrors().forEach((e) -> {
                responseErrorList.add(ResponseError.of((FieldError) e));
            });

            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
        }

        ServiceResult serviceResult = reservationService.makeReservation(reservationDto, token);
        return ResponseResult.result(serviceResult);

    }

    // API 매장 도착 확인
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmArrival(@RequestBody @Valid ReservationCheckDto reservationCheckDto, @RequestHeader("STORE-TOKEN") String token) {

        ServiceResult serviceResult = reservationService.confirmArrival(token, reservationCheckDto.getReservationTime());

        return ResponseResult.result(serviceResult);
    }
}
