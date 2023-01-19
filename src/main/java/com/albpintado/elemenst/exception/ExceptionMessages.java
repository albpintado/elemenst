package com.albpintado.elemenst.exception;

public enum ExceptionMessages {
    INVALID_PASSWORD("Password must have between 8 and 18 characters and include at least one uppercase letter, one lowercase letter, one number and one special character");

    private String message;

    ExceptionMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
