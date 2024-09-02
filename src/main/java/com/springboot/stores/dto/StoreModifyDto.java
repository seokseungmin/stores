package com.springboot.stores.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StoreModifyDto {
    @NotBlank
    private String name;

    @NotBlank
    private String newName;

    @NotBlank
    private String location;

    @NotBlank
    private String description;

    @NotNull
    private Double latitude;   // 위도

    @NotNull
    private Double longitude;  // 경도

}