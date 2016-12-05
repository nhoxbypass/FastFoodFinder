package com.example.mypc.fastfoodfinder.model;

/**
 * Created by MyPC on 12/5/2016.
 */
public class ListPacket {
    String name;
    int idIconSource;

    public ListPacket(String name, int idIconSource) {
        this.name = name;
        this.idIconSource = idIconSource;
    }

    public String getName() {
        return name;
    }

    public int getIdIconSource() {
        return idIconSource;
    }
}
