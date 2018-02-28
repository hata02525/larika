package com.example.fluper.larika_user_app.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rohit on 3/7/17.
 */

public class AddCartMainModel {
    static AddCartMainModel addCartMainModel;
    private String deleteCart;

    public static AddCartMainModel getAddCartMainModel() {
        return addCartMainModel;
    }

    public static void setAddCartMainModel(AddCartMainModel addCartMainModel) {
        AddCartMainModel.addCartMainModel = addCartMainModel;
    }

    public String getDeleteCart() {
        return deleteCart;
    }

    public void setDeleteCart(String deleteCart) {
        this.deleteCart = deleteCart;
    }

    public  List<AddCartModel>cart;

    public List<AddCartModel> getCart() {
        if(cart==null)
            cart=new ArrayList<>();
        return cart;
    }
    public static AddCartMainModel getInstance()
    {
        if(addCartMainModel==null)
        {
            addCartMainModel=new AddCartMainModel();
        }
        return addCartMainModel;

    }


    public void setCart(List<AddCartModel> cart) {

        this.cart = cart;
    }
}
