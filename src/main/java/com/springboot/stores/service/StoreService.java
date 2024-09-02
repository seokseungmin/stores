package com.springboot.stores.service;

import com.springboot.stores.common.model.ServiceResult;
import com.springboot.stores.dto.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;


public interface StoreService {

    ServiceResult registerStore(StoreRegistrationDto storeDto, String token);
    ServiceResult modifyStore(StoreModifyDto storeDto, String token);
    ServiceResult deleteStore(StoreDeleteDto storeDeleteDto, String token);
    List<StoreDto> searchStores(String search, double latitude, double longitude, String sortBy);
    List<ReservationStateCheckDto> checkReservations(String token, LocalDate date);
}