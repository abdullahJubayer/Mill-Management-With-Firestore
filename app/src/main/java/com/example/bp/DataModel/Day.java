package com.example.bp.DataModel;

public class Day {
    private String day;
    private String name;
    private String email;
    private String amount;
    public Day() {
    }

    public Day(String day, String name, String email) {
        this.day = day;
        this.name = name;
        this.email = email;
    }

    public Day(String day, String name, String email, String amount) {
        this.day = day;
        this.name = name;
        this.email = email;
        this.amount = amount;
    }

    public String getDay() {
        return day;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAmount() {
        return amount;
    }
}
