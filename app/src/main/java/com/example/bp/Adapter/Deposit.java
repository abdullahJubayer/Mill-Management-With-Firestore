package com.example.bp.Adapter;

public class Deposit {
    private String date;
    private String amount;

    public Deposit() {
    }

    public Deposit(String date, String amount) {
        this.date = date;
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public String getAmount() {
        return amount;
    }
}
