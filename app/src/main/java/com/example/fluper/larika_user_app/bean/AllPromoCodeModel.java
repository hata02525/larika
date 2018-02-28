package com.example.fluper.larika_user_app.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fluper-pc on 17/8/17.
 */

public class AllPromoCodeModel {
    private String message;
    private List<AllPromoCodeResultModel>result;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<AllPromoCodeResultModel> getResult() {
        if (result==null)
            result=new ArrayList<>();
        return result;
    }

    public void setResult(List<AllPromoCodeResultModel> result) {
        this.result = result;
    }
}
