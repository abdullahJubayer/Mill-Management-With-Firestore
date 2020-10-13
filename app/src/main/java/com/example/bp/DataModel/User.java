package com.example.bp.DataModel;

import java.util.List;
import java.util.Map;

public class User {
    private String email;
    private String name;
    private String balance;
    private double mill;
    private List<Mill> mills;

    public User() {
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

    public List<Mill> getMills() {
        return mills;
    }

    public void setMills(List<Mill> mills) {
        this.mills = mills;
    }
}
