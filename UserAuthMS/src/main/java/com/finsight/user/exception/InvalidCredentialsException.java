package com.finsight.user.exception;

public class InvalidCredentialsException extends Exception {

    private String message;

    public InvalidCredentialsException(String message){
        super(message);
        System.out.println("SomeThing Bad has happened in the request body");
    }

}
