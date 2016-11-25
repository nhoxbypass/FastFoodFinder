package com.example.mypc.fastfoodfinder.model;

import java.io.Serializable;

/**
 * Created by MyPC on 11/21/2016.
 */
public class Article implements Serializable {

    String des;
    String address;

    public String getDes() {
        return des;
    }

    public String getAddress() {
        return address;
    }

    public Article (String d, String a){
        des = d;
        address = a;
    }
}
