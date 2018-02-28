package com.example.fluper.larika_user_app.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rohit on 30/6/17.
 */

public class PaymentCardMainModel {
    private List<AddPaymentCardModel> addCardModelList;

    public List<AddPaymentCardModel> getAddCardModelList() {
        if(addCardModelList==null)
            addCardModelList=new ArrayList<>();
        return addCardModelList;
    }

    public void setAddCardModelList(List<AddPaymentCardModel> addCardModelList) {
        this.addCardModelList = addCardModelList;
    }
}
