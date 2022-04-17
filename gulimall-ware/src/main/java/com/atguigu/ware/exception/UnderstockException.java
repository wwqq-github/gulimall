package com.atguigu.ware.exception;

public class UnderstockException extends RuntimeException{
    public UnderstockException() {
    }

    public UnderstockException(String message) {
        super(message);
    }

    public UnderstockException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnderstockException(Throwable cause) {
        super(cause);
    }

    public UnderstockException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
