package de.skyslycer.hmcwraps.commands.exception;

public class InvalidWrapException extends RuntimeException {

    public InvalidWrapException(String wrap) {
        super(wrap);
    }

}
