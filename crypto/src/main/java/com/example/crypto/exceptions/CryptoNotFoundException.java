package com.example.crypto.exceptions;

public class CryptoNotFoundException extends RuntimeException{

    public CryptoNotFoundException(String message){
        super(message);
    }

}
