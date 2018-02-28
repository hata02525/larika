package com.example.fluper.larika_user_app.bean;

/**
 * Created by rohit on 30/6/17.
 */

public class AddProductModel {
    public String dishId;
    public String vendorId;
    public String dishName;
    public String dishTitle;
    public String dishDesc;
    public String dishImage;
    public float dishRating;
    public String dishStock;
    public String dishSummry;
    public String dishPrice;
    public String venderName;
    private int quantity=1;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDishId() {
        return dishId;
    }

    public void setDishId(String dishId) {
        this.dishId = dishId;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getDishTitle() {
        return dishTitle;
    }

    public void setDishTitle(String dishTitle) {
        this.dishTitle = dishTitle;
    }

    public String getDishDesc() {
        return dishDesc;
    }

    public void setDishDesc(String dishDesc) {
        this.dishDesc = dishDesc;
    }

    public String getDishImage() {
        return dishImage;
    }

    public void setDishImage(String dishImage) {
        this.dishImage = dishImage;
    }

    public float getDishRating() {
        return dishRating;
    }

    public void setDishRating(float dishRating) {
        this.dishRating = dishRating;
    }

    public String getDishStock() {
        return dishStock;
    }

    public void setDishStock(String dishStock) {
        this.dishStock = dishStock;
    }

    public String getDishSummry() {
        return dishSummry;
    }

    public void setDishSummry(String dishSummry) {
        this.dishSummry = dishSummry;
    }

    public String getDishPrice() {
        return dishPrice;
    }

    public void setDishPrice(String dishPrice) {
        this.dishPrice = dishPrice;
    }

    public String getVenderName() {
        return venderName;
    }

    public void setVenderName(String venderName) {
        this.venderName = venderName;
    }
}
