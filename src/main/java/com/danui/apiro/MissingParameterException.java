package com.danui.apiro;

public class MissingParameterException extends Exception {
    public MissingParameterException() {
        super();
    }
    public MissingParameterException(String message) {
        super(message);
    }
    public MissingParameterException(String message, Throwable cause) {
        super(message, cause);
    }
    public MissingParameterException(Throwable cause) {
        super(cause);
    }
}
