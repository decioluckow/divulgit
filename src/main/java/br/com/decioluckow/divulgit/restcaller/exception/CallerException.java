package br.com.decioluckow.divulgit.restcaller.exception;

public class CallerException extends Exception {

    public CallerException(String message) {
        super(message);
    }

    public CallerException(String message, Throwable cause) {
        super(message, cause);
    }
}
