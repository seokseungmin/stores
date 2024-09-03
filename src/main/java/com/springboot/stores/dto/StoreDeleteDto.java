package com.springboot.stores.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StoreDeleteDto {
    @NotBlank
    private String name;
}
