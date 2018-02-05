package ru.fargus.testapp.ui.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ru.fargus.testapp.R;
import ru.fargus.testapp.model.City;
import ru.fargus.testapp.model.Location;
import ru.fargus.testapp.ui.base.BasePresenter;
import ru.fargus.testapp.ui.map.constants.MapConfig;

/**
 * Created by Дмитрий on 31.01.2018.
 */

public class MapPresenter<T extends MapView> implements BasePresenter<T> {

    private T mView;
    private LatLng preLatLng;
    private float mAngle;
    private Marker mPlaneIcon;
    private Bitmap mMarkerIcon;
    private int mIndexCurrentPoint;
    private boolean isMarkerRotating = false;
    private CompositeDisposable mCompositeDisposable;
    private List<LatLng> mRoutePointsList = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public MapPresenter(T view){
        mView = view;
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void attachView(T baseView) {
        mView = baseView;
    }

    @Override
    public void detachView() {
        clearDisposables();
        mView = null;
    }


    // Disposables
    @Override
    public void addDisposable(Disposable disposable) {}

    private void clearDisposables(){
        if (!mCompositeDisposable.isDisposed())
            mCompositeDisposable.clear();
    }

    // Getters
    public LatLng getMiddlePoint(Location location1, Location location2 ){

        double lat1 = Math.toRadians(location1.getLat());
        double lat2 = Math.toRadians(location2.getLat());
        double lon1 = Math.toRadians(location1.getLon());
        double dLon = Math.toRadians(location2.getLon() - location1.getLon());

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double middleLat= Math.atan2(Math.sin(lat1) + Math.sin(lat2),
                Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double middleLon = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        mAngle = angleFromCoordinate(location1.getLat(), location1.getLon(),
                location2.getLat(), location2.getLon());

        return new LatLng(middleLat, middleLon);
    }


    private float angleFromCoordinate(double lat1, double long1, double lat2,
                                       double long2) {
        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(x, y);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        brng = 360 - brng;
        return (float)brng;
    }

    public String getIataCode(City city) {
        if (city.getIata().size() > 0) {
            return city.getIata().get(0);
        } else {
            return "";
        }
    }

    public LatLng getDeparturePoint(City departureCity){
        Location departureLocation = departureCity.getLocation();
        return new LatLng(departureLocation.getLat(), departureLocation.getLon());
    }

    public LatLng getArrivalPoint(City arrivalCity) {
        Location arrivalLocation = arrivalCity.getLocation();
        return new LatLng(arrivalLocation.getLat(), arrivalLocation.getLon());
    }



    // set Route params
    public PolylineOptions getRouteForPoints(LatLng departurePoint, LatLng arrivalPoint) {
        mRoutePointsList.clear();
        mRoutePointsList.add(departurePoint);
        mRoutePointsList.add(arrivalPoint);

        return new PolylineOptions()
                .add(departurePoint, arrivalPoint)
                .color(Color.GRAY)
                .geodesic(false);
    }

    public void setRouteStyle(Polyline polyline){
        polyline.setJointType(JointType.ROUND);
        polyline.setPattern(MapConfig.PATTERN_DOTTED);
        polyline.setWidth(MapConfig.MAP_POLYLINE_STROKE_WIDTH_PX);
    }


    private Bitmap createMapMarkerView(View markerLayout, String title) {

            TextView markerRating = (TextView) markerLayout.findViewById(R.id.map_city_iata_code);
            markerRating.setText(title);

            markerLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            markerLayout.layout(0, 0, markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight());

            final Bitmap bitmap = Bitmap.createBitmap(markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            markerLayout.draw(canvas);
            return bitmap;
    }

    public MarkerOptions addMapMarker(LatLng point, String title, View markerView){
        return new MarkerOptions()
                .position(point)
                .icon(BitmapDescriptorFactory.fromBitmap(createMapMarkerView(markerView, title)));
    }




    // Animation

    public void moveMarkerAnimation(final GoogleMap map, Bitmap icon, final LatLng beginLatLng, final LatLng endLatLng, final long duration) {
        final Handler handler = new Handler();
        final long startTime = SystemClock.uptimeMillis();

        final Interpolator interpolator = new LinearInterpolator();

        // set car bearing for current part of path
        mMarkerIcon = icon;
        mPlaneIcon = map.addMarker(new MarkerOptions()
                .position(beginLatLng)
                .anchor(0.0f , 0.0f)
                .zIndex(1.0f)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_plane)));
        float angleDeg = (float)(180 * getAngle(beginLatLng, endLatLng) / Math.PI);
        Matrix matrix = new Matrix();
        matrix.postRotate(angleDeg);
        mPlaneIcon.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createBitmap(mMarkerIcon, 0, 0, mMarkerIcon.getWidth(), mMarkerIcon.getHeight(), matrix, true)));


        handler.post(new Runnable() {
            @Override
            public void run() {
                // calculate phase of animation
                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                // calculate new position for marker
                double lat = (endLatLng.latitude - beginLatLng.latitude) * t + beginLatLng.latitude;
                double lngDelta = endLatLng.longitude - beginLatLng.longitude;

                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * t + beginLatLng.longitude;

                mPlaneIcon.setPosition(new LatLng(lat, lng));

                // if not end of line segment of path
                if (t < 1.0) {
                    // call next marker position
                    handler.postDelayed(this, 16);
                } else {
                    // call turn animation
                    nextTurnAnimation();
                }
            }
        });
    }


    private void moveMarkerAnimation(final Marker marker, final LatLng beginLatLng, final LatLng endLatLng, final long duration) {
        final Handler handler = new Handler();
        mIndexCurrentPoint = 0;
        final long startTime = SystemClock.uptimeMillis();

        final Interpolator interpolator = new LinearInterpolator();

        // set car bearing for current part of path
        float angleDeg = (float)(180 * getAngle(beginLatLng, endLatLng) / Math.PI);
        Matrix matrix = new Matrix();
        matrix.postRotate(angleDeg);
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createBitmap(mMarkerIcon, 0, 0, mMarkerIcon.getWidth(), mMarkerIcon.getHeight(), matrix, true)));

        handler.post(new Runnable() {
            @Override
            public void run() {
                // calculate phase of animation
                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                // calculate new position for marker
                double lat = (endLatLng.latitude - beginLatLng.latitude) * t + beginLatLng.latitude;
                double lngDelta = endLatLng.longitude - beginLatLng.longitude;

                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * t + beginLatLng.longitude;

                marker.setPosition(new LatLng(lat, lng));

                // if not end of line segment of path
                if (t < 1.0) {
                    // call next marker position
                    handler.postDelayed(this, 16);
                } else {
                    // call turn animation
                    nextTurnAnimation();
                }
            }
        });
    }


    private void turnMarkerAnimation(final Marker marker, final float startAngle, final float endAngle, final long duration) {
        final Handler handler = new Handler();
        final long startTime = SystemClock.uptimeMillis();
        final Interpolator interpolator = new LinearInterpolator();

        final float dAndgle = endAngle - startAngle;

        Matrix matrix = new Matrix();
        matrix.postRotate(startAngle);
        Bitmap rotatedBitmap = Bitmap.createBitmap(mMarkerIcon, 0, 0, mMarkerIcon.getWidth(), mMarkerIcon.getHeight(), matrix, true);
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(rotatedBitmap));

        handler.post(new Runnable() {
            @Override
            public void run() {

                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = interpolator.getInterpolation((float) elapsed / duration);

                Matrix m = new Matrix();
                m.postRotate(startAngle + dAndgle * t);
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createBitmap(mMarkerIcon, 0, 0, mMarkerIcon.getWidth(), mMarkerIcon.getHeight(), m, true)));

                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                } else {
                    nextMoveAnimation();
                }
            }
        });
    }


    private void nextTurnAnimation() {
        mIndexCurrentPoint++;

        if (mIndexCurrentPoint < mRoutePointsList.size() - 1) {
            LatLng prevLatLng = mRoutePointsList.get(mIndexCurrentPoint - 1);
            LatLng currLatLng = mRoutePointsList.get(mIndexCurrentPoint);
            LatLng nextLatLng = mRoutePointsList.get(mIndexCurrentPoint + 1);

            float beginAngle = (float)(180 * getAngle(prevLatLng, currLatLng) / Math.PI);
            float endAngle = (float)(180 * getAngle(currLatLng, nextLatLng) / Math.PI);
            turnMarkerAnimation(mPlaneIcon, beginAngle, endAngle, 3000);
        }
    }

    private void nextMoveAnimation() {
        if (mIndexCurrentPoint <  mRoutePointsList.size() - 1) {
            moveMarkerAnimation(mPlaneIcon, mRoutePointsList.get(mIndexCurrentPoint), mRoutePointsList.get(mIndexCurrentPoint+1), 4000);
        }
    }

    private double getAngle(LatLng beginLatLng, LatLng endLatLng) {
        double f1 = Math.PI * beginLatLng.latitude / 180;
        double f2 = Math.PI * endLatLng.latitude / 180;
        double dl = Math.PI * (endLatLng.longitude - beginLatLng.longitude) / 180;
        return Math.atan2(Math.sin(dl) * Math.cos(f2) , Math.cos(f1) * Math.sin(f2) - Math.sin(f1) * Math.cos(f2) * Math.cos(dl));
    }

}
