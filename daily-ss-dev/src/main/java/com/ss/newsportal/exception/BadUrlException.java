package com.ss.newsportal.exception;


public class BadUrlException extends RuntimeException {

    public BadUrlException(String message) {
        super(message);
    }
}