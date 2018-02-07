package ru.fargus.testapp.ui.map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindBitmap;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.fargus.testapp.R;
import ru.fargus.testapp.helpers.ToastHelper;
import ru.fargus.testapp.model.City;
import ru.fargus.testapp.ui.map.constants.MapConfig;

public class MapActivity extends AppCompatActivity implements IMapView, OnMapReadyCallback {

    GoogleMap mMapView;
    private City mArrivalCity;
    private City mDepartureCity;
    private Marker mPlaneMarker;
    private Unbinder mViewsUnbinder;
    private MapPresenter mMapPresenter;
    @BindBitmap(R.mipmap.ic_plane) Bitmap mMapMarker;


    public static void buildIntent(Activity activity, Bundle extraParams) {
        if (activity != null) {
            Intent intent = new Intent(activity, MapActivity.class);
            intent.putExtras(extraParams);
            activity.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mViewsUnbinder = ButterKnife.bind(this);

        Intent intent = getIntent();
        mMapPresenter = new MapPresenter(this);
        if (intent != null && intent.getExtras() != null) {
            Bundle arguments = intent.getExtras();
            mArrivalCity = Parcels.unwrap(arguments.getParcelable(MapConfig.MAP_ARRIVAL_PARAM));
            mDepartureCity = Parcels.unwrap(arguments.getParcelable(MapConfig.MAP_DEPARTURE_PARAM));
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMapView = googleMap;

        LatLng mArrivalPoint = mMapPresenter.getArrivalPoint(mArrivalCity);
        LatLng mDeparturePoint = mMapPresenter.getDeparturePoint(mDepartureCity);
        mMapView.moveCamera(CameraUpdateFactory.newLatLngZoom(mDeparturePoint, 3));


        View planeLayout = getLayoutInflater().inflate(R.layout.plane_marker_layout, null);
        View arrivalLayout = getLayoutInflater().inflate(R.layout.airport_marker_layout, null);
        View departureLayout = getLayoutInflater().inflate(R.layout.airport_marker_layout, null);

        mPlaneMarker = mMapView.addMarker(mMapPresenter.setPlaneMarker(mDeparturePoint,  planeLayout));
        mMapView.addMarker(mMapPresenter.setAirportMarker(mArrivalPoint, mMapPresenter.getIataCode(mArrivalCity), arrivalLayout));
        mMapView.addMarker(mMapPresenter.setAirportMarker(mDeparturePoint, mMapPresenter.getIataCode(mDepartureCity), departureLayout));

        mMapPresenter.buildRoutePoints(mDeparturePoint, mArrivalPoint);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewsUnbinder.unbind();
    }

    @Override
    public void showToastMessage(String errorMessage) {
        ToastHelper.showToastMessage(this, errorMessage);
    }

    @Override
    public void buildRouteOnMap(List<LatLng> points) {
        Polyline polyline = mMapView.addPolyline(new PolylineOptions().addAll(points));
        mMapPresenter.setPolylineStyle(polyline);

        mMapPresenter.animateMarkerMoveAlongRoute(mPlaneMarker, points);
    }
}
