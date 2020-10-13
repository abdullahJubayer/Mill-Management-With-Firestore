package com.example.bp.DataModel;

public class Mill {
    private String date;
    private String mill;

    public Mill() {
    }

    public Mill(String date, String mill) {
        this.date = date;
        this.mill = mill;
    }

    public String getDate() {
        return date;
    }

    public String getMill() {
        return mill;
    }
}
