package io.github.vishwajiths2005.lafit_backend.models;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "The same user can't make a claim.")
public class SameUserException extends RuntimeException {
    public SameUserException(String message) {
        super(message);
    }
}
