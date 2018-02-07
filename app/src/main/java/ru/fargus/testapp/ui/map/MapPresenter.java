package ru.fargus.testapp.ui.map;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.fargus.testapp.R;
import ru.fargus.testapp.model.City;
import ru.fargus.testapp.model.Location;
import ru.fargus.testapp.ui.base.BasePresenter;
import ru.fargus.testapp.ui.map.constants.MapConfig;
import ru.fargus.testapp.ui.map.util.LatLngEvaluator;
import ru.fargus.testapp.ui.map.util.MapUtils;

/**
 * Created by Дмитрий on 31.01.2018.
 */

public class MapPresenter<T extends IMapView> implements BasePresenter<T> {

    private T mView;
    private CompositeDisposable mCompositeDisposable;
    private List<LatLng> mRoutePointsList = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public MapPresenter(T view) {
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
    public void addDisposable(Disposable disposable) {
    }

    private void clearDisposables() {
        if (!mCompositeDisposable.isDisposed())
            mCompositeDisposable.clear();
    }

    // Getters
    public LatLng getMiddlePoint(Location location1, Location location2) {

        double lat1 = Math.toRadians(location1.getLat());
        double lat2 = Math.toRadians(location2.getLat());
        double lon1 = Math.toRadians(location1.getLon());
        double dLon = Math.toRadians(location2.getLon() - location1.getLon());

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double middleLat = Math.atan2(Math.sin(lat1) + Math.sin(lat2),
                Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double middleLon = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

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
        return (float) brng;
    }

    public String getIataCode(City city) {
        if (city.getIata().size() > 0) {
            return city.getIata().get(0);
        } else {
            return "";
        }
    }

    public LatLng getDeparturePoint(City departureCity) {
        Location departureLocation = departureCity.getLocation();
        return new LatLng(departureLocation.getLat(), departureLocation.getLon());
    }

    public LatLng getArrivalPoint(City arrivalCity) {
        Location arrivalLocation = arrivalCity.getLocation();
        return new LatLng(arrivalLocation.getLat(), arrivalLocation.getLon());
    }



    public void setPolylineStyle(Polyline polyline) {
        polyline.setZIndex(0.5f);
        polyline.setColor(Color.BLUE);
        polyline.setJointType(JointType.ROUND);
        polyline.setPattern(MapConfig.PATTERN_DOTTED);
        polyline.setWidth(MapConfig.MAP_POLYLINE_STROKE_WIDTH_PX);
    }

    public void setMarkerMovingStyle(Marker marker, LatLng end) {
        marker.setZIndex(1.0f);
        marker.setAnchor(1.0f, 1.0f);
        marker.setRotation((float) MapUtils.computeAngleBetween(marker.getPosition(), end));
//        marker.setRotation(-((float) SphericalUtil.computeHeading(marker.getPosition(), end)));
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_plane));
    }




    public void buildRoutePoints(LatLng startPoint, LatLng endPoint) {
        mRoutePointsList.clear();
        mRoutePointsList.add(startPoint);
        mRoutePointsList.add(endPoint);

        Observable.just(mRoutePointsList)
                .flatMap(this::interpolateRoutePoints)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(latLngs -> {
                    if (latLngs.size() > 0) {
                        mView.buildRouteOnMap(latLngs);
                    }
                }, throwable -> mView.showToastMessage(throwable.getLocalizedMessage()));
    }


    // Создание маркера для начальной или конечной точки маршрута
    public MarkerOptions setAirportMarker(LatLng point, String title, View markerView) {
        return new MarkerOptions()
                .position(point)
                .zIndex(0.5f)
                .icon(BitmapDescriptorFactory.fromBitmap(buildAirportMarkerBitmap(markerView, title)));
    }

    private Bitmap buildAirportMarkerBitmap(View markerLayout, String title) {

        TextView markerRating = (TextView) markerLayout.findViewById(R.id.map_city_iata_code);
        markerRating.setText(title);

        markerLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        markerLayout.layout(0, 0, markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight());

        final Bitmap bitmap = Bitmap.createBitmap(markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        markerLayout.draw(canvas);
        return bitmap;
    }



    private Observable<List<LatLng>> interpolateRoutePoints(List<LatLng> points) {

        LatLng curveLatLng;
        List<LatLng> middlePointsList = new ArrayList<>();

        LatLng init = points.get(0);
        LatLng end = points.get(points.size() - 1);

        double distanceBetween = SphericalUtil.computeDistanceBetween(init, end);
        double lineHeadingInit = SphericalUtil.computeHeading(init, end);

        double lineHeading1, lineHeading2;
        if (lineHeadingInit < 0) {
            lineHeading1 = lineHeadingInit + 45;
            lineHeading2 = lineHeadingInit + 135;
        } else {
            lineHeading1 = lineHeadingInit + -45;
            lineHeading2 = lineHeadingInit + -135;
        }

        LatLng pA = SphericalUtil.computeOffset(init, distanceBetween / 2.5, lineHeading1);
        LatLng pB = SphericalUtil.computeOffset(end, distanceBetween / 2.5, lineHeading2);

        for (double t = 0.0; t < 1.01; t += 0.01) {
            double arcX = Math.pow((1 - t), 3) * init.latitude
                    + 3 * Math.pow((1 - t), 2) * t * pA.latitude
                    + 3 * (1 - t) * Math.pow(t, 2) * pB.latitude
                    + Math.pow(t, 3) * end.latitude;
            double arcY = Math.pow((1 - t), 3) * init.longitude
                    + 3 * Math.pow((1 - t), 2) * t * pA.longitude
                    + 3 * (1 - t) * Math.pow(t, 2) * pB.longitude
                    + Math.pow(t, 3) * end.longitude;


            curveLatLng = new LatLng(arcX, arcY);
            middlePointsList.add(curveLatLng);
        }

        return Observable.just(middlePointsList);
    }


    private static float computeRotation(float fraction, float start, float end) {
        float normalizeEnd = end - start; // rotate start to 0
        float normalizedEndAbs = (normalizeEnd + 360) % 360;

        float direction = (normalizedEndAbs > 180) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
        float rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }

        float result = fraction * rotation + start;
        return (result + 360) % 360;
    }




    // Animation

    private double getAngle(LatLng beginLatLng, LatLng endLatLng) {
        double f1 = Math.PI * beginLatLng.latitude / 180;
        double f2 = Math.PI * endLatLng.latitude / 180;
        double dl = Math.PI * (endLatLng.longitude - beginLatLng.longitude) / 180;
        return Math.atan2(Math.sin(dl) * Math.cos(f2) , Math.cos(f1) * Math.sin(f2) - Math.sin(f1) * Math.cos(f2) * Math.cos(dl));
    }



    public void animateMarkerMoveForRoute(Marker mapMarker, List<LatLng> middlePointsList) {

        Object[] pointsArray = middlePointsList.toArray();

        ValueAnimator markerAnimator = ValueAnimator.ofObject(new LatLngEvaluator(), pointsArray);
        markerAnimator.setDuration(6000);
        markerAnimator.addUpdateListener(valueAnimator -> {
            LatLng nextPosition = (LatLng) valueAnimator.getAnimatedValue();
            float bearing = (float) MapUtils.computeAngleBetween(mapMarker.getPosition(), nextPosition);
//            float bearing = angleFromCoordinate(mapMarker.getPosition().latitude, mapMarker.getPosition().longitude,
//                    nextPosition.latitude, nextPosition.longitude);

            mapMarker.setRotation(bearing);
            mapMarker.setPosition(nextPosition);
        });
        markerAnimator.start();
    }

}
