package com.jakitrans.mc.json.payment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jakitrans.mc.models.payment.Transaksi;

import java.util.List;

public class GetHistoryResponse {
    @Expose
    @SerializedName("code")
    private String code;

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("data")
    private List<Transaksi> transaksiList;

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

    public List<Transaksi> getTransaksiList() {
        return transaksiList;
    }

    public void setTransaksiList(List<Transaksi> transaksiList) {
        this.transaksiList = transaksiList;
    }
}
