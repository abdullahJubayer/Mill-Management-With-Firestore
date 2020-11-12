package com.example.bp.DataModel;

public class RecyclerModel {
    private String data;
    private String value;

    public RecyclerModel() {
    }

    public RecyclerModel(String data, String value) {
        this.data = data;
        this.value = value;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getData() {
        return data;
    }

    public String getValue() {
        return value;
    }
}
