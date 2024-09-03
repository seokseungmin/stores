package com.springboot.stores.controller;

import com.springboot.stores.common.model.ResponseError;
import com.springboot.stores.common.model.ResponseResult;
import com.springboot.stores.common.model.ServiceResult;
import com.springboot.stores.dto.StoreDeleteDto;
import com.springboot.stores.dto.StoreDto;
import com.springboot.stores.dto.StoreModifyDto;
import com.springboot.stores.dto.StoreRegistrationDto;
import com.springboot.stores.service.StoreService;
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
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    // 파트너 권한이 있는 사용자만 매장 등록가능
    @PostMapping("/register")
    public ResponseEntity<?> registerStore(@RequestBody @Valid StoreRegistrationDto storeDto, @RequestHeader("STORE-TOKEN") String token, Errors errors) {


        List<ResponseError> responseErrorList = new ArrayList<>();

        if (errors.hasErrors()) {
            errors.getAllErrors().forEach((e) -> {
                responseErrorList.add(ResponseError.of((FieldError) e));
            });

            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
        }

        ServiceResult serviceResult = storeService.registerStore(storeDto, token);
        return ResponseResult.result(serviceResult);

    }

    //파트너 권한이 있는 사람만 자신의 매장정보 수정
    @PutMapping("/modify")
    public ResponseEntity<?> modifyStore(@RequestBody @Valid StoreModifyDto storeModifyDto, @RequestHeader("STORE-TOKEN") String token, Errors errors) {
        List<ResponseError> responseErrorList = new ArrayList<>();

        if (errors.hasErrors()) {
            errors.getAllErrors().forEach((e) -> {
                responseErrorList.add(ResponseError.of((FieldError) e));
            });

            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
        }

        ServiceResult serviceResult = storeService.modifyStore(storeModifyDto, token);
        return ResponseResult.result(serviceResult);

    }

    //매장 점주 본인이고, 파트너 권한이 있는 사람만 삭제 가능
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteStore(@RequestBody @Valid StoreDeleteDto storeDeleteDto, @RequestHeader("STORE-TOKEN") String token, Errors errors) {
        List<ResponseError> responseErrorList = new ArrayList<>();

        if (errors.hasErrors()) {
            errors.getAllErrors().forEach((e) -> {
                responseErrorList.add(ResponseError.of((FieldError) e));
            });

            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
        }

        ServiceResult serviceResult = storeService.deleteStore(storeDeleteDto, token);
        return ResponseResult.result(serviceResult);

    }

    // 매장 이름이나 매장이름을 포함하는 단어가 있으면 찾아서
    // 가나다(name)순, 별점순(rating), 거리(distance)순으로 매장 상세 정보를 가진 매장 리스트를 반환
    @GetMapping
    public ResponseEntity<?> getStores(@RequestParam(required = false) String search,
                                       @RequestParam double latitude,
                                       @RequestParam double longitude,
                                       @RequestParam String sortBy) {
        List<StoreDto> stores = storeService.searchStores(search, latitude, longitude, sortBy);

        if (stores.isEmpty()) {
            return ResponseResult.fail("해당 이름의 매장이 없습니다.");
        }
        return ResponseResult.success("매장 리스트 반환 성공!", stores);
    }


}