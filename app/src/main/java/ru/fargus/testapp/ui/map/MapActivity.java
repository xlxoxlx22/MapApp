package ru.fargus.testapp.ui.map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
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

import ru.fargus.testapp.R;
import ru.fargus.testapp.helpers.ToastHelper;
import ru.fargus.testapp.model.City;
import ru.fargus.testapp.ui.map.constants.MapConfig;

public class MapActivity extends FragmentActivity implements MapView, OnMapReadyCallback {

    private GoogleMap mMap;
    private City arrivalCity;
    private Bitmap mMapMarker;
    private City departureCity;
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

        Intent intent = getIntent();
        mMapPresenter = new MapPresenter(this);
        if (intent != null && intent.getExtras() != null) {
            Bundle arguments = intent.getExtras();
            arrivalCity = Parcels.unwrap(arguments.getParcelable(MapConfig.MAP_ARRIVAL_PARAM));
            departureCity = Parcels.unwrap(arguments.getParcelable(MapConfig.MAP_DEPARTURE_PARAM));
        }

        mMapMarker = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_plane);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng arrivalPoint = mMapPresenter.getArrivalPoint(arrivalCity);
        LatLng departurePoint = mMapPresenter.getDeparturePoint(departureCity);
        Polyline route = mMap.addPolyline(mMapPresenter.getRouteForPoints(departurePoint, arrivalPoint));

        mMapPresenter.setRouteStyle(route);

        Location source = new Location(LocationManager.GPS_PROVIDER);
        source.setLatitude(departurePoint.latitude);
        source.setLongitude(departurePoint.longitude);

        View arrivalLayout = getLayoutInflater().inflate(R.layout.map_marker_layout, null);
        View departureLayout = getLayoutInflater().inflate(R.layout.map_marker_layout, null);
        Marker startPoint = mMap.addMarker(mMapPresenter.addMapMarker(arrivalPoint, mMapPresenter.getIataCode(arrivalCity), arrivalLayout));
        Marker endPoint = mMap.addMarker(mMapPresenter.addMapMarker(departurePoint, mMapPresenter.getIataCode(departureCity), departureLayout));
        Marker planeMarker = mMap.addMarker(new MarkerOptions()
                .position(startPoint.getPosition())
                .anchor(0.0f , 0.0f)
                .zIndex(1.0f)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_plane)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mMapPresenter.getMiddlePoint(departureCity.getLocation(), arrivalCity.getLocation())));

        mMapPresenter.animateMarker(endPoint, planeMarker);

//        mMapPresenter.moveMarkerAnimation(mMap, mMapMarker,departurePoint, arrivalPoint, 5000);

    }


    @Override
    public void showToastMessage(String errorMessage) {
        ToastHelper.showToastMessage(this, errorMessage);
    }
}
