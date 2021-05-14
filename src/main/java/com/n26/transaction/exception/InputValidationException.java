package com.n26.transaction.exception;


public class InputValidationException extends RuntimeException {

    public InputValidationException(String errorCode) {
        super();
        this.errorCode=errorCode;
    }

    private String errorCode;

    public String getErrorCode() {
        return errorCode;
    }


}
