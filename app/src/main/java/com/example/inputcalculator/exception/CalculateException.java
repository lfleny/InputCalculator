package com.example.inputcalculator.exception;

public class CalculateException extends RuntimeException {
    public CalculateException(String message) {
        super(message);
    }

    public CalculateException(String message, Throwable cause) {
        super(message, cause);
    }
}
