package com.awshackathon.goforpic.exception;

public class ResponseException extends Exception {

    public ResponseException() {
        super();
    }

    public ResponseException(String message) {
        super(message);
    }

    public ResponseException(Throwable cause) {
        super(cause);
    }
}
