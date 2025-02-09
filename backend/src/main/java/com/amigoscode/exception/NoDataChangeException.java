package com.amigoscode.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class NoDataChangeException extends RuntimeException {

    public NoDataChangeException(String message) {
        super(message);
    }
}
