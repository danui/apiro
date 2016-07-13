package com.danui.apiro;

public class WrongParameterTypeException extends Exception {
    public WrongParameterTypeException() {
        super();
    }
    public WrongParameterTypeException(String message) {
        super(message);
    }
    public WrongParameterTypeException(String message, Throwable cause) {
        super(message, cause);
    }
    public WrongParameterTypeException(Throwable cause) {
        super(cause);
    }
}
