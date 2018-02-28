package com.example.fluper.larika_user_app.bean;

import java.util.List;

/**
 * Created by rohit on 7/6/17.
 */

public class OrderMainModel {
    String message;
    List<OrderResults>result;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<OrderResults> getResult() {
        return result;
    }

    public void setResult(List<OrderResults> result) {
        this.result = result;
    }
}
