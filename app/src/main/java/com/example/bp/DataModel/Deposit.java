package com.example.bp.DataModel;

public class Deposit {
    private String amount;
    private String date;

    public Deposit(String amount, String date) {
        this.amount = amount;
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
