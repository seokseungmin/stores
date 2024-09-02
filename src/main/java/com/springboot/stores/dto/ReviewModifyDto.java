package com.springboot.stores.dto;

import lombok.Data;

@Data
public class ReviewModifyDto {
    private Long reviewId;  // 수정할 리뷰의 ID
    private int rating;
    private String comment;
}
