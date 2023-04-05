package com.example.nfc_huseyin_pasa_demir.service;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CardDao {
    @Insert()
    void insert(Card card);
    @Query("SELECT * FROM card_table WHERE cardNumber LIKE :cardNumber ")
    Card findCard(String cardNumber);
}