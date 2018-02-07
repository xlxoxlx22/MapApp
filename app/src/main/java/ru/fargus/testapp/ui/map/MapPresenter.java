package ru.fargus.testapp.ui.map;

import android.animation.PointFEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

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
import ru.fargus.testapp.ui.map.util.DoubleArrayEvaluator;

/**
 * Created by Дмитрий on 31.01.2018.
 */

public class MapPresenter<T extends IMapView> implements BasePresenter<T> {

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
    public PolylineOptions setRouteForPoints(List<LatLng> points) {
        return new PolylineOptions()
                .addAll(points)
                .color(Color.BLUE);
    }

    public void setRouteStyle(Polyline polyline){
        polyline.setJointType(JointType.ROUND);
        polyline.setPattern(MapConfig.PATTERN_DOTTED);
        polyline.setWidth(MapConfig.MAP_POLYLINE_STROKE_WIDTH_PX);
    }

    public void buildRoutePoints(LatLng startPoint, LatLng endPoint){
        mRoutePointsList.clear();
        mRoutePointsList.add(startPoint);
        mRoutePointsList.add(endPoint);
        Observable.just(mRoutePointsList)
                .flatMap(latLngs -> interpolateRoutePoints(latLngs))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(latLngs -> {
                    if (latLngs.size() > 0) {
                        mView.buildRouteOnMap(latLngs);
                    }
                }, throwable -> mView.showToastMessage(throwable.getLocalizedMessage()));
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
                .zIndex(0.5f)
                .icon(BitmapDescriptorFactory.fromBitmap(createMapMarkerView(markerView, title)));
    }



    public void getMapVisualRect(GoogleMap map, Window window) {
        LatLngBounds visibleRegion = map.getProjection().getVisibleRegion().latLngBounds;
        Point topLeft = new Point(0, 0);
        Point bottomRight = new Point(window.getDecorView().getRight(), window.getDecorView().getBottom());


    }



    private Observable<List<LatLng>> interpolateRoutePoints(List<LatLng> points){
        List<LatLng> routePoints = new ArrayList<>();
        LatLng startPosition = points.get(0);
        LatLng endPosition = points.get(points.size()-1);
        Interpolator interpolator = new LinearInterpolator();

        routePoints.add(startPosition);

        for (double t = 0.0; t < 1.01; t += 0.11) {
            double lat = t * endPosition.latitude + (1-t) * startPosition.latitude;
            double lng = t * endPosition.longitude + (1-t) * startPosition.longitude;

            LatLng intermediatePosition = new LatLng(lat, lng);
            routePoints.add(intermediatePosition);
        }

        routePoints.add(endPosition);
        return Observable.just(routePoints);
    }


    public void animateMarker(List<LatLng> points, Marker movingMarker) {
        if (movingMarker != null) {

            LatLng lastPoint = points.get(points.size() - 1);

                double[] startValues = new double[]{movingMarker.getPosition().latitude, movingMarker.getPosition().longitude};
                double[] endValues = new double[]{lastPoint.latitude, lastPoint.longitude};
                ValueAnimator latLngAnimator = ValueAnimator.ofObject(new DoubleArrayEvaluator(), startValues, endValues);
                latLngAnimator.setDuration(200);
                latLngAnimator.setInterpolator(new DecelerateInterpolator());
                latLngAnimator.addUpdateListener(animation ->  {
                    for ( int i = 1; i < points.size(); i++) {
                        double[] animatedValue = (double[]) animation.getAnimatedValue();
                        movingMarker.setPosition(new LatLng(animatedValue[0], animatedValue[1]));
                    }
                });
                latLngAnimator.start();
        }

//            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
//            valueAnimator.setDuration(MapConfig.MAP_MARKER_MOVEMENT_ANIMATION_DURATION); // duration 1 second
//            valueAnimator.setInterpolator(new LinearInterpolator());
//            valueAnimator.addUpdateListener(animation -> {
//                for ( LatLng point : points) {
//                    movingMarker.setPosition(point);
//                }
//            });
//            valueAnimator.start();

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

        public void animateMarker(Marker m,final boolean hideMarke) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final long duration = 5000;

            final LatLng startLatLng = mRoutePointsList.get(0);
            final LatLng toPosition = mRoutePointsList.get(mRoutePointsList.size() -1);
            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed
                            / duration);
                    double lng = t * toPosition.longitude + (1 - t)
                            * startLatLng.longitude;
                    double lat = t * toPosition.latitude + (1 - t)
                            * startLatLng.latitude;
                    m.setPosition(new LatLng(lat, lng));

                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        if (hideMarke) {
                            m.setVisible(false);
                        } else {
                            m.setVisible(true);
                        }
                    }
                }
            });
        }

    private double getAngle(LatLng beginLatLng, LatLng endLatLng) {
        double f1 = Math.PI * beginLatLng.latitude / 180;
        double f2 = Math.PI * endLatLng.latitude / 180;
        double dl = Math.PI * (endLatLng.longitude - beginLatLng.longitude) / 180;
        return Math.atan2(Math.sin(dl) * Math.cos(f2) , Math.cos(f1) * Math.sin(f2) - Math.sin(f1) * Math.cos(f2) * Math.cos(dl));
    }

}
