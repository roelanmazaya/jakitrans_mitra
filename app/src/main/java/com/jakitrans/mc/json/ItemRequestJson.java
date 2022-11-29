package com.jakitrans.mc.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Ourdevelops Team on 10/13/2019.
 */

public class ItemRequestJson {

    @SerializedName("no_telepon")
    @Expose
    private String notelepon;

    @SerializedName("idmerchant")
    @Expose
    private String idmerchant;

    @SerializedName("idkategori")
    @Expose
    private String idkategori;



    public String getNotelepon() {
        return notelepon;
    }

    public void setNotelepon(String notelepon) {
        this.notelepon = notelepon;
    }

    public String getIdmerchant() {
        return idmerchant;
    }

    public void setIdmerchant(String idmerchant) {
        this.idmerchant = idmerchant;
    }

    public String getIdkategori() {
        return idkategori;
    }

    public void setIdkategori(String idkategori) {
        this.idkategori = idkategori;
    }


}
