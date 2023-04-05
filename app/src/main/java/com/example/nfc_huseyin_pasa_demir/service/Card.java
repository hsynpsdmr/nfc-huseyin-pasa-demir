package com.example.nfc_huseyin_pasa_demir.service;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "card_table")
public class Card {
    @PrimaryKey(autoGenerate = true)
    private int cardID;
    private String cardNumber;
    private String expireDate;
    private String cardType;
    @Ignore
    public Card(String cardNumber, String expireDate, String cardType) {
        this.cardNumber = cardNumber;
        this.expireDate = expireDate;
        this.cardType = cardType;
    }

    public Card(int cardID, String cardNumber, String expireDate, String cardType) {
        this.cardID = cardID;
        this.cardNumber = cardNumber;
        this.expireDate = expireDate;
        this.cardType = cardType;
    }

    public int getCardID() {
        return cardID;
    }

    public void setCardID(int cardID) {
        this.cardID = cardID;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }
}
