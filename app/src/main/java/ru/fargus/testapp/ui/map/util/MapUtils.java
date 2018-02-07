package ru.fargus.testapp.ui.map.util;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class MapUtils {

    private float bearingBetweenLatLngs(LatLng beginLatLng,LatLng endLatLng) {
        Location beginLocation = convertLatLngToLocation(beginLatLng);
        Location endLocation = convertLatLngToLocation(endLatLng);
        return beginLocation.bearingTo(endLocation);
    }

    private Location convertLatLngToLocation(LatLng latLng) {
        Location location = new Location("someLoc");
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        return location;
    }

    private static LatLng midPoint(LatLng p1, LatLng p2) throws IllegalArgumentException{

        if(p1 == null || p2 == null)
            throw new IllegalArgumentException("two points are needed for calculation");

        double lat1;
        double lon1;
        double lat2;
        double lon2;

        //convert to radians
        lat1 = Math.toRadians(p1.latitude);
        lon1 = Math.toRadians(p1.longitude);
        lat2 = Math.toRadians(p2.latitude);
        lon2 = Math.toRadians(p2.longitude);

        double x1 = Math.cos(lat1) * Math.cos(lon1);
        double y1 = Math.cos(lat1) * Math.sin(lon1);
        double z1 = Math.sin(lat1);

        double x2 = Math.cos(lat2) * Math.cos(lon2);
        double y2 = Math.cos(lat2) * Math.sin(lon2);
        double z2 = Math.sin(lat2);

        double x = (x1 + x2)/2;
        double y = (y1 + y2)/2;
        double z = (z1 + z2)/2;

        double lon = Math.atan2(y, x);
        double hyp = Math.sqrt(x*x + y*y);

        // HACK: 0.9 and 1.1 was found by trial and error; this is probably *not* the right place to apply mid point shifting
        double lat = Math.atan2(.9*z, hyp);
        if(lat>0) lat = Math.atan2(1.1*z, hyp);

        return new LatLng(Math.toDegrees(lat),  Math.toDegrees(lon));
    }

}
