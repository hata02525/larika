package com.example.fluper.larika_user_app.bean;

/**
 * Created by rohit on 4/7/17.
 */
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class AddMyProductMainModel {

    @Expose
    private String message;
    @Expose
    private List<AddMyProductModel> result = null;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<AddMyProductModel> getResult() {
        if(result==null)
            result=new ArrayList<>();
        return result;
    }

    public void setResult(List<AddMyProductModel> result) {
        this.result = result;
    }

}