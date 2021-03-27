package com.ss.newsportal.exception;


public class NotNullColumnException extends RuntimeException{
    public NotNullColumnException(String message) {
        super(message);
    }
}
