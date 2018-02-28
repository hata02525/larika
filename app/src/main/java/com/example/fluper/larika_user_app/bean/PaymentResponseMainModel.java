package com.example.fluper.larika_user_app.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rohit on 1/9/17.
 */

public class PaymentResponseMainModel {
    private List<PaymentCardServerResponse>result;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<PaymentCardServerResponse> getResult() {
        if(result==null)
            result=new ArrayList<>();
        return result;
    }

    public void setResult(List<PaymentCardServerResponse> result) {
        this.result = result;
    }
}