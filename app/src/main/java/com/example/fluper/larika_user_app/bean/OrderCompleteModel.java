package com.example.fluper.larika_user_app.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fluper-pc on 17/8/17.
 */

public class OrderCompleteModel {
    private String message;
    private List<OrderCompleteResultModel>result;
    private boolean closeApi=false;

    public boolean isCloseApi() {
        return closeApi;
    }

    public void setCloseApi(boolean closeApi) {
        this.closeApi = closeApi;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<OrderCompleteResultModel> getResult() {
        if(result==null)
            result=new ArrayList<>();
        return result;
    }

    public void setResult(List<OrderCompleteResultModel> result) {
        this.result = result;
    }
}
