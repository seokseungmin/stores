package com.springboot.stores.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StoreDeleteDto {
    @NotBlank
    private String name;
}
