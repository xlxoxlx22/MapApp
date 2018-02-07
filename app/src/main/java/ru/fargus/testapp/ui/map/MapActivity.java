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
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

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


    @BindBitmap(R.mipmap.ic_plane) Bitmap mMapMarker;


    GoogleMap mMapView;
    private City arrivalCity;
    private City departureCity;
    private LatLng mArrivalPoint;
    private LatLng mDeparturePoint;
    private Projection mProjection;
    private Unbinder mViewsUnbinder;
    private MapPresenter mMapPresenter;


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
            arrivalCity = Parcels.unwrap(arguments.getParcelable(MapConfig.MAP_ARRIVAL_PARAM));
            departureCity = Parcels.unwrap(arguments.getParcelable(MapConfig.MAP_DEPARTURE_PARAM));
        }

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMapView = googleMap;

        mArrivalPoint = mMapPresenter.getArrivalPoint(arrivalCity);
        mDeparturePoint = mMapPresenter.getDeparturePoint(departureCity);
        mMapPresenter.buildRoutePoints(mDeparturePoint, mArrivalPoint);

        mMapView.moveCamera(CameraUpdateFactory.newLatLng(mDeparturePoint));
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
        Polyline route = mMapView.addPolyline(mMapPresenter.setRouteForPoints(points));
        mMapPresenter.setRouteStyle(route);


        View arrivalLayout = getLayoutInflater().inflate(R.layout.map_marker_layout, null);
        View departureLayout = getLayoutInflater().inflate(R.layout.map_marker_layout, null);
        Marker endPoint = mMapView.addMarker(mMapPresenter.addMapMarker(mArrivalPoint, mMapPresenter.getIataCode(arrivalCity), arrivalLayout));
        Marker startPoint = mMapView.addMarker(mMapPresenter.addMapMarker(mDeparturePoint, mMapPresenter.getIataCode(departureCity), departureLayout));
        Marker planeMarker = mMapView.addMarker(new MarkerOptions()
                .position(startPoint.getPosition())
                .anchor(1.0f , 1.0f)
                .zIndex(1.0f)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_plane)));

        mMapPresenter.animateMarker(points, planeMarker);

    }
}
