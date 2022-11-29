package com.jakitrans.mc.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jakitrans.mc.models.ItemModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ourdevelops Team on 10/13/2019.
 */

public class ItemResponseJson {

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private List<ItemModel> data = new ArrayList<>();



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ItemModel> getData() {
        return data;
    }

    public void setData(List<ItemModel> data) {
        this.data = data;
    }


}