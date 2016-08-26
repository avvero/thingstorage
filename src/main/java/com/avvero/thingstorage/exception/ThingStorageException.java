package com.avvero.thingstorage.exception;

/**
 * Created by avvero on 26.08.2016.
 */
public class ThingStorageException extends RuntimeException {
    public ThingStorageException() {
    }

    public ThingStorageException(String message) {
        super(message);
    }

    public ThingStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public ThingStorageException(Throwable cause) {
        super(cause);
    }
}
