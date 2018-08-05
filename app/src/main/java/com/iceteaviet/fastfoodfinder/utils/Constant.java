package com.iceteaviet.fastfoodfinder.utils;

import com.google.android.gms.maps.model.LatLng;

/**
 * Use class instead of interface for constant class to avoid Constant Interface Antipattern
 *
 * @see https://stackoverflow.com/questions/2659593/what-is-the-use-of-interface-constants
 * <p>
 * Created by Genius Doan on 11/11/2016.
 */

final public class Constant {
    //Type
    public static final int TYPE_CIRCLE_K = 0;
    public static final int TYPE_MINI_STOP = 1;
    public static final int TYPE_FAMILY_MART = 2;
    public static final int TYPE_BSMART = 3;
    public static final int TYPE_SHOP_N_GO = 4;

    //Map utils
    public static final long MAPS_INTERVAL = 1000 * 10;
    public static final long MAPS_FASTEST_INTERVAL = 1000 * 5;

    public static final String DOWNLOADER_BOT_EMAIL = "store_downloader@fastfoodfinder.com";
    public static final String DOWNLOADER_BOT_PWD = "123456789";

    public static final String NO_AVATAR_PLACEHOLDER_URL = "http://cdn.builtlean.com/wp-content/uploads/2015/11/all_noavatar.png.png";

    public static final float DEFAULT_ZOOM_LEVEL = 16;
    public static final float DETAILED_ZOOM_LEVEL = 18;
    public static final LatLng DEFAULT_MAP_TARGET = new LatLng(10.773996, 106.6898035);

    private Constant() {

    }
}
