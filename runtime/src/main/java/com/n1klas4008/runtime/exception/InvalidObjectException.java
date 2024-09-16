package com.n1klas4008.runtime.exception;

public class InvalidObjectException extends RuntimeException {

    public InvalidObjectException(Object o) {
        super("Invalid Object: " + o.toString());
    }
}
