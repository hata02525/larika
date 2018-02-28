package com.example.fluper.larika_user_app.bean;

/**
 * Created by fluper-pc on 3/8/17.
 */

import java.util.List;


public class OnTheGoModel {
    private String message;
    private List<OnTheGoResultModel>result;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<OnTheGoResultModel> getResult() {
        return result;
    }

    public void setResult(List<OnTheGoResultModel> result) {
        this.result = result;
    }
}