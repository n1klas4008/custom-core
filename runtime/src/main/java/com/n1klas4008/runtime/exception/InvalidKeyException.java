package com.n1klas4008.runtime.exception;

public class InvalidKeyException extends Exception {

    public InvalidKeyException(String key) {
        super("Specified key [" + key + "] is not present");
    }
}
