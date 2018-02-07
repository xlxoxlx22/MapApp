package ru.fargus.testapp.ui.map.util;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import static java.lang.Math.*;

public class MapUtils {

    public static float bearingBetweenLatLngs(LatLng beginLatLng,LatLng endLatLng) {
        Location beginLocation = convertLatLngToLocation(beginLatLng);
        Location endLocation = convertLatLngToLocation(endLatLng);
        return beginLocation.bearingTo(endLocation);
    }

    private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }

    public static double computeAngleBetween(LatLng p1, LatLng p2) {

        double fromLat = Math.toRadians(p1.latitude);
        double fromLng = Math.toRadians(p1.longitude);
        double toLat = Math.toRadians(p2.latitude);
        double toLng = Math.toRadians(p2.longitude);

        double dLat = fromLat - toLat;
        double dLng = fromLng - toLng;
        return 2 * asin(sqrt(pow(sin(dLat / 2), 2) +
                cos(fromLat) * cos(toLat) * pow(sin(dLng / 2), 2)));
    }

    private static Location convertLatLngToLocation(LatLng latLng) {
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

        double x1 = cos(lat1) * cos(lon1);
        double y1 = cos(lat1) * sin(lon1);
        double z1 = sin(lat1);

        double x2 = cos(lat2) * cos(lon2);
        double y2 = cos(lat2) * sin(lon2);
        double z2 = sin(lat2);

        double x = (x1 + x2)/2;
        double y = (y1 + y2)/2;
        double z = (z1 + z2)/2;

        double lon = Math.atan2(y, x);
        double hyp = sqrt(x*x + y*y);

        // HACK: 0.9 and 1.1 was found by trial and error; this is probably *not* the right place to apply mid point shifting
        double lat = Math.atan2(.9*z, hyp);
        if(lat>0) lat = Math.atan2(1.1*z, hyp);

        return new LatLng(Math.toDegrees(lat),  Math.toDegrees(lon));
    }

}
