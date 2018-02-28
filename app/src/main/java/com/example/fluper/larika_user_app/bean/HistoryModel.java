package com.example.fluper.larika_user_app.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 31-07-2017.
 */

public class HistoryModel {
    private String message;
    private List<HistorResultModel> result;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<HistorResultModel> getResult() {
        if(result==null)
            result=new ArrayList<>();
        return result;
    }

    public void setResult(List<HistorResultModel> result) {
        this.result = result;
    }
}
