package org.sprouts.tss.diff.exception;

public class DiffException extends RuntimeException {

    public DiffException(Throwable e) {
        super(e);
    }

    public DiffException(String message) {
        super(message);
    }

    public DiffException(String message, Throwable e) {
        super(message + ": " + e.getMessage());
    }
}
