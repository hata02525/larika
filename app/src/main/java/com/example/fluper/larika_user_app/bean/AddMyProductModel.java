package com.example.fluper.larika_user_app.bean;

/**
 * Created by rohit on 4/7/17.
 */

public class AddMyProductModel {



    private String dishId,quantity;

    private String vendorDishId;

    private String vendorId;

    private String dishName;


    private String dishTitle;


    private String dishDesc;


    private String dishImage;

    private String rating;


    private String dishStock;


    private String dishSummary;


    private String dishPrice;


    private String vendorName;


    private String category;


    private String nature;
    private String cartId;

    boolean isMatchId;

    public boolean isMatchId() {
        return isMatchId;
    }

    public void setMatchId(boolean matchId) {
        isMatchId = matchId;
    }

    public String getCartId() {
        if(cartId==null)
            cartId="";
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getQuantity() {
        if(quantity==null)
            quantity="1";
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDishId() {
        return dishId;
    }

    public void setDishId(String dishId) {
        this.dishId = dishId;
    }

    public String getVendorDishId() {
        return vendorDishId;
    }

    public void setVendorDishId(String vendorDishId) {
        this.vendorDishId = vendorDishId;
    }

    public String getVendorId() {
        if(vendorId==null)
            vendorId="";
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

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDishStock() {
        return dishStock;
    }

    public void setDishStock(String dishStock) {
        this.dishStock = dishStock;
    }

    public String getDishSummary() {
        return dishSummary;
    }

    public void setDishSummary(String dishSummary) {
        this.dishSummary = dishSummary;
    }

    public String getDishPrice() {
        return dishPrice;
    }

    public void setDishPrice(String dishPrice) {
        this.dishPrice = dishPrice;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

}