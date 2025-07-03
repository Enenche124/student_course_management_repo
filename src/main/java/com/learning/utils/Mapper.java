package com.learning.utils;



import com.learning.dtos.responses.LoginResponse;
import com.learning.dtos.responses.RegisterResponse;

public class Mapper {



    public static RegisterResponse mapRegister(String message, boolean success) {
        return new RegisterResponse(message, success);
    }

    public static LoginResponse mapLogin(String message, boolean success, String token, String  role, String name    ) {
        return new LoginResponse(message, success, token, role, name);
    }
}