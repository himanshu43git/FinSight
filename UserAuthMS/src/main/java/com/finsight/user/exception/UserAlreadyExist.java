package com.finsight.user.exception;

public class UserAlreadyExist extends Throwable {

    private String message;

    public UserAlreadyExist(String message){
        super(message);
        System.out.println("EXCEPTION -->>> USER ALREADY EXIST BY EMAIL OR ID");
    }

}
