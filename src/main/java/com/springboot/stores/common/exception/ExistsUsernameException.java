package com.springboot.stores.common.exception;

public class ExistsUsernameException extends RuntimeException {
    public ExistsUsernameException(String s) {
        super(s);
    }
}
