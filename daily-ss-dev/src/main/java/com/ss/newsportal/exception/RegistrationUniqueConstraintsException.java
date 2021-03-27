package com.ss.newsportal.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class RegistrationUniqueConstraintsException extends RuntimeException {
    private final List<String> errors;

    public RegistrationUniqueConstraintsException(List<String> errors) {
        super();
        this.errors = errors;
    }
}
