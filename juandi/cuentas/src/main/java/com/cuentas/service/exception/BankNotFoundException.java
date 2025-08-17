package com.cuentas.service.exception;

public class BankNotFoundException extends RuntimeException {

    public BankNotFoundException(String message) {
        super(message);
    }
}
