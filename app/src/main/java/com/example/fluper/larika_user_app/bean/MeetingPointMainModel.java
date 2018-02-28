package com.example.fluper.larika_user_app.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rohit on 6/7/17.
 */

public class MeetingPointMainModel {
    private String message;

    private List<MeetingPointResultModel>result;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public List<MeetingPointResultModel> getResult() {
        if(result==null)
            result=new ArrayList<>();
        return result;
    }

    public void setResult(List<MeetingPointResultModel> result) {
        this.result = result;
    }
}
