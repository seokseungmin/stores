package com.springboot.stores.service;

import com.springboot.stores.common.model.ServiceResult;
import com.springboot.stores.dto.ReviewDeleteDto;
import com.springboot.stores.dto.ReviewDto;
import com.springboot.stores.dto.ReviewModifyDto;

public interface ReviewService {
    ServiceResult addReview(ReviewDto reviewDto, String token);

    ServiceResult modifyReview(ReviewModifyDto reviewModifyDto, String token);

    ServiceResult deleteReview(ReviewDeleteDto reviewDeleteDto, String token);
}