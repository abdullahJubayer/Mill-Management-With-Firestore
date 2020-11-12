package com.example.bp.DataModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String email;
    private String name;
    private String balance;
    private double mill;
    private Map<String,String> mills;
    private Map<String,String> deposits;

    public User (){

    }


    public User(String email, String name, Map<String, String> mills, Map<String, String> deposits) {
        this.email = email;
        this.name = name;
        this.mills = mills;
        this.deposits = deposits;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public double getMill() {
        return mill;
    }

    public void setMill(double mill) {
        this.mill = mill;
    }

    public Map<String, String> getMills() {
        return mills;
    }

    public Map<String, String> getDeposits() {
        return deposits;
    }
}
