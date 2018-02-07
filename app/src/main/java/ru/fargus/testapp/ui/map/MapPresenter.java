package ru.fargus.testapp.ui.map;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptor;
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

    // Setters
    public void setPolylineStyle(Polyline polyline) {
        polyline.setZIndex(0.5f);
        polyline.setColor(Color.BLUE);
        polyline.setJointType(JointType.ROUND);
        polyline.setPattern(MapConfig.PATTERN_DOTTED);
        polyline.setWidth(MapConfig.MAP_POLYLINE_STROKE_WIDTH_PX);
    }


    // Создание маркера для начальной или конечной точки маршрута
    public MarkerOptions setAirportMarker(LatLng point, String title, View markerView) {
        return new MarkerOptions()
                .position(point)
                .zIndex(0.5f)
                .icon(BitmapDescriptorFactory.fromBitmap(buildAirportMarkerBitmap(markerView, title)));
    }

    public MarkerOptions setPlaneMarker(LatLng point, View markerView) {
        return new MarkerOptions()
                .position(point)
                .zIndex(1.0f)
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromBitmap(buildPlaneMarkerBitmap(markerView)));
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

    private Bitmap buildPlaneMarkerBitmap(View markerLayout) {
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

        for (double t = 0.0; t < 1.01; t += 0.0001) {
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



    // Animation
 private LatLng previousPosition;
    public void animateMarkerMoveAlongRoute(Marker mapMarker, List<LatLng> middlePointsList) {

        previousPosition =  mapMarker.getPosition();
        Object[] pointsArray = middlePointsList.toArray();

        ValueAnimator markerAnimator = ValueAnimator.ofObject(new LatLngEvaluator(), pointsArray);
        markerAnimator.setDuration(MapConfig.MAP_MARKER_MOVEMENT_ANIMATION_DURATION);
        markerAnimator.addUpdateListener(valueAnimator -> {

            LatLng nextPosition = (LatLng) valueAnimator.getAnimatedValue();
            float bearing = MapUtils.getBearing(previousPosition, nextPosition);

            mapMarker.setRotation(bearing);
            mapMarker.setPosition(nextPosition);

            previousPosition = nextPosition;
        });
        markerAnimator.start();
    }

}
