package com.n26.exception;

public class TransactionException extends Exception {

    private static final long serialVersionUID = 1L;

    TransactionException() {
    }

    public TransactionException(String message){ super(message); }

    public TransactionException(String mesage, Throwable cause) { super(mesage,cause); }
}

