package com.iceteaviet.fastfoodfinder.data.local.poi.model;

import android.location.Location;
import android.support.annotation.DrawableRes;

/**
 * Created by Genius Doan on 20/07/2017.
 */

public class AugmentedPOI {
    private Location location;
    private String name;
    @DrawableRes
    private int iconId;

    public AugmentedPOI(String name, double lat, double lon, double altitude, @DrawableRes int icon) {
        this.name = name;
        location = new Location(name);
        location.setLatitude(lat);
        location.setLongitude(lon);
        location.setAltitude(altitude);
        this.iconId = icon;
    }

    public Location getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public int getIcon() {
        return iconId;
    }
}
