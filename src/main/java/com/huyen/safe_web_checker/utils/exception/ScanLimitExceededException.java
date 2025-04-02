package com.huyen.safe_web_checker.utils.exception;

public class ScanLimitExceededException extends RuntimeException {
    public ScanLimitExceededException(String message) {
        super(message);
    }
}