package com.ksh.exception;

public class SqlUpdateFailureException extends RuntimeException {
    public SqlUpdateFailureException(String message){
        super(message);
    }

    public SqlUpdateFailureException(String message, Throwable cause){
        super(message, cause);
    }
}
