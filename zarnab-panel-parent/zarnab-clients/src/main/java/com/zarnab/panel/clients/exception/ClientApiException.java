package com.zarnab.panel.clients.exception;

public class ClientApiException extends RuntimeException {
    public ClientApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientApiException(String message) {
        super(message);
    }

    public static class Client4xxException extends ClientApiException {
        public Client4xxException(String message) {
            super(message);
        }
    }

    public static class Client5xxException extends ClientApiException {
        public Client5xxException(String message) {
            super(message);
        }
    }
}