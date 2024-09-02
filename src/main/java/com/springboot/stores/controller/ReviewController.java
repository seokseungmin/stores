package com.springboot.stores.controller;

import com.springboot.stores.common.model.ResponseError;
import com.springboot.stores.common.model.ResponseResult;
import com.springboot.stores.common.model.ServiceResult;
import com.springboot.stores.dto.ReviewDeleteDto;
import com.springboot.stores.dto.ReviewDto;
import com.springboot.stores.dto.ReviewModifyDto;
import com.springboot.stores.service.ReviewService;
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
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    // API 리뷰 작성
    @PostMapping
    public ResponseEntity<?> addReview(@RequestBody @Valid ReviewDto reviewDto, @RequestHeader("STORE-TOKEN") String token, Errors errors) {
        List<ResponseError> responseErrorList = new ArrayList<>();

        if (errors.hasErrors()) {
            errors.getAllErrors().forEach((e) -> {
                responseErrorList.add(ResponseError.of((FieldError) e));
            });

            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
        }

        ServiceResult serviceResult = reviewService.addReview(reviewDto, token);
        return ResponseResult.result(serviceResult);
    }

    // API 리뷰 수정
    @PutMapping
    public ResponseEntity<?> modifyReview(@RequestBody @Valid ReviewModifyDto reviewModifyDto, @RequestHeader("STORE-TOKEN") String token, Errors errors) {
        List<ResponseError> responseErrorList = new ArrayList<>();

        if (errors.hasErrors()) {
            errors.getAllErrors().forEach((e) -> {
                responseErrorList.add(ResponseError.of((FieldError) e));
            });

            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
        }

        ServiceResult serviceResult = reviewService.modifyReview(reviewModifyDto, token);
        return ResponseResult.result(serviceResult);
    }

    // API 리뷰 삭제
    @DeleteMapping
    public ResponseEntity<?> deleteReview(@RequestBody @Valid ReviewDeleteDto reviewDeleteDto, @RequestHeader("STORE-TOKEN") String token, Errors errors) {
        List<ResponseError> responseErrorList = new ArrayList<>();

        if (errors.hasErrors()) {
            errors.getAllErrors().forEach((e) -> {
                responseErrorList.add(ResponseError.of((FieldError) e));
            });

            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
        }
        ServiceResult serviceResult = reviewService.deleteReview(reviewDeleteDto, token);
        return ResponseResult.result(serviceResult);
    }

}