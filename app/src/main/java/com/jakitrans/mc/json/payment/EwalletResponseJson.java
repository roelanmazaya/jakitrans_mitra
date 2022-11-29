package com.jakitrans.mc.json.payment;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jakitrans.mc.models.payment.Ewallet;


public class EwalletResponseJson {
    @Expose
    @SerializedName("code")
    private String code;

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("data")
    private Ewallet model;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Ewallet getModel() {
        return model;
    }

    public void setModel(Ewallet model) {
        this.model = model;
    }
}
