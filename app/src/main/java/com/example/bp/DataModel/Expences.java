package com.example.bp.DataModel;

public class Expences {
    private String name;
    private String email;
    private String date;
    private String amount;
    private String totalExpences;

    public Expences() {
    }

    public Expences(String name, String email, String date, String amount) {
        this.name = name;
        this.email = email;
        this.date = date;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTotalExpences() {
        return totalExpences;
    }

    public void setTotalExpences(String totalExpences) {
        this.totalExpences = totalExpences;
    }
}
