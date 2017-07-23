package com.iceteaviet.fastfoodfinder.utils;

import android.location.Location;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Genius Doan on 20/07/2017.
 */

final public class LocationUtils {
    private final static double WGS84_A = 6378137.0;                  // WGS 84 semi-major axis constant in meters
    private final static double WGS84_E2 = 0.00669437999014;          // square of WGS 84 eccentricity

    private LocationUtils() {

    }

    public static LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Constant.INTERVAL);
        mLocationRequest.setFastestInterval(Constant.FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return mLocationRequest;
    }

    public static String getLatLngString(LatLng latLng) {
        if (latLng != null)
            return String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude);
        else
            return null;
    }

    public static double calcDistance(LatLng startPosition, LatLng endPosition) {
        //
        Location start = new Location("pointA");
        start.setLatitude(startPosition.latitude);
        start.setLongitude(startPosition.longitude);
        Location end = new Location("pointB");
        end.setLatitude(endPosition.latitude);
        end.setLongitude(endPosition.longitude);

        return (start.distanceTo(end) / 1000.0);
    }

    public static float[] WSG84toECEF(Location location) {
        double radLat = Math.toRadians(location.getLatitude());
        double radLon = Math.toRadians(location.getLongitude());

        float clat = (float) Math.cos(radLat);
        float slat = (float) Math.sin(radLat);
        float clon = (float) Math.cos(radLon);
        float slon = (float) Math.sin(radLon);

        float N = (float) (WGS84_A / Math.sqrt(1.0 - WGS84_E2 * slat * slat));

        float x = (float) ((N + location.getAltitude()) * clat * clon);
        float y = (float) ((N + location.getAltitude()) * clat * slon);
        float z = (float) ((N * (1.0 - WGS84_E2) + location.getAltitude()) * slat);

        return new float[]{x, y, z};
    }

    public static float[] ECEFtoENU(Location currentLocation, float[] ecefCurrentLocation, float[] ecefPOI) {
        double radLat = Math.toRadians(currentLocation.getLatitude());
        double radLon = Math.toRadians(currentLocation.getLongitude());

        float clat = (float) Math.cos(radLat);
        float slat = (float) Math.sin(radLat);
        float clon = (float) Math.cos(radLon);
        float slon = (float) Math.sin(radLon);

        float dx = ecefCurrentLocation[0] - ecefPOI[0];
        float dy = ecefCurrentLocation[1] - ecefPOI[1];
        float dz = ecefCurrentLocation[2] - ecefPOI[2];

        float east = -slon * dx + clon * dy;

        float north = -slat * clon * dx - slat * slon * dy + clat * dz;

        float up = clat * clon * dx + clat * slon * dy + slat * dz;

        return new float[]{east, north, up, 1};
    }
}
