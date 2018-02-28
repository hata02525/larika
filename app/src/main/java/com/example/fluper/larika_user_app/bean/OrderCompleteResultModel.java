package com.example.fluper.larika_user_app.bean;

/**
 * Created by fluper-pc on 17/8/17.
 */

public class OrderCompleteResultModel {
    private String isConfirmed;

    public String getIsConfirmed() {
        if(isConfirmed==null)
            isConfirmed="";
        return isConfirmed;
    }

    public void setIsConfirmed(String isConfirmed) {
        this.isConfirmed = isConfirmed;
    }
}
