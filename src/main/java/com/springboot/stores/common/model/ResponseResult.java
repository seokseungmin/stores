package com.springboot.stores.common.model;


import org.springframework.http.ResponseEntity;

public class ResponseResult {

    public static ResponseEntity<?> fail(String message) {
        return fail(message, null);
    }

    public static ResponseEntity<?> fail(String message, Object data) {
        return ResponseEntity.badRequest().body(ResponseMessage.fail(message, data));
    }

    public static ResponseEntity<?> success(String message) {
        return success(message, null);
    }

    public static ResponseEntity<?> success(String message, Object data) {
        return ResponseEntity.ok().body(ResponseMessage.success(message, data));
    }

    public static ResponseEntity<?> result(ServiceResult result) {
        if (result.isFail()) {
            return fail(result.getMessage());
        }
        return success(result.getMessage());
    }

}
