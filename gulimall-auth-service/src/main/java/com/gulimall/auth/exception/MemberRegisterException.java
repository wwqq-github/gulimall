package com.gulimall.auth.exception;

public class MemberRegisterException extends RuntimeException{
    public MemberRegisterException() {
    }

    public MemberRegisterException(String message) {
        super(message);
    }

    public MemberRegisterException(String message, Throwable cause) {
        super(message, cause);
    }

    public MemberRegisterException(Throwable cause) {
        super(cause);
    }

    public MemberRegisterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
