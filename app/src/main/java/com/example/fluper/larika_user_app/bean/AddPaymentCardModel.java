package com.example.fluper.larika_user_app.bean;

/**
 * Created by rohit on 30/6/17.
 */

public class AddPaymentCardModel {
    private String cardNumber;
    private String cardExpiry;
    private String cardCvv;
    private String cardUserName;

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    private String cardId;
    private boolean isSelected =false;

    public boolean isSelected() {
        return isSelected;
    }

    public boolean setSelected(boolean selected) {
        isSelected = selected;
        return selected;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardExpiry() {
        return cardExpiry;
    }

    public void setCardExpiry(String cardExpiry) {
        this.cardExpiry = cardExpiry;
    }

    public String getCardCvv() {
        return cardCvv;
    }

    public void setCardCvv(String cardCvv) {
        this.cardCvv = cardCvv;
    }

    public String getCardUserName() {
        return cardUserName;
    }

    public void setCardUserName(String cardUserName) {
        this.cardUserName = cardUserName;
    }
}