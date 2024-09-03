package com.springboot.stores.controller;

import com.springboot.stores.common.model.ResponseError;
import com.springboot.stores.common.model.ResponseResult;
import com.springboot.stores.common.model.ServiceResult;
import com.springboot.stores.dto.ReservationCheckDto;
import com.springboot.stores.dto.ReservationDto;
import com.springboot.stores.dto.ReservationStateCheckDto;
import com.springboot.stores.service.ReservationService;
import com.springboot.stores.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final StoreService storeService;

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

    // API 매장 점주 예약 정보 리스트 확인
    @PostMapping("/checkReservations")
    public ResponseEntity<?> checkReservations(@RequestHeader("STORE-TOKEN") String token,
                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<ReservationStateCheckDto> stores = storeService.checkReservations(token, date);
        return ResponseResult.success("예약체크 리스트 반환 성공!", stores);
    }

    // API 예약 승인
    @PostMapping("/{reservationId}/approve")
    public ResponseEntity<?> approveReservation(@PathVariable Long reservationId, @RequestHeader("STORE-TOKEN") String token) {

        ServiceResult result = reservationService.approveReservation(reservationId, token);
        return ResponseResult.success(result.getMessage());
    }

    // API 예약 거절
    @PostMapping("/{reservationId}/reject")
    public ResponseEntity<?> rejectReservation(@PathVariable Long reservationId, @RequestHeader("STORE-TOKEN") String token) {

        ServiceResult result = reservationService.rejectReservation(reservationId, token);
        return ResponseResult.success(result.getMessage());
    }
}
