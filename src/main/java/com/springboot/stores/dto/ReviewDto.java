package com.springboot.stores.dto;

import lombok.Data;

@Data
public class ReviewDto {
    private String storeName;
    private int rating;
    private String comment;
}
