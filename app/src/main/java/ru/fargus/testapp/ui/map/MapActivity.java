package ru.fargus.testapp.ui.map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.parceler.Parcels;

import ru.fargus.testapp.R;
import ru.fargus.testapp.helpers.ToastHelper;
import ru.fargus.testapp.model.City;
import ru.fargus.testapp.model.Location;
import ru.fargus.testapp.ui.map.constants.MapConfig;

public class MapActivity extends FragmentActivity implements MapView, OnMapReadyCallback {

    private GoogleMap mMap;
    private City arrivalCity;
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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Location arrivalLocation = arrivalCity.getLocation();
        Location departureLocation = departureCity.getLocation();

        LatLng arrivalPoint = new LatLng(arrivalLocation.getLat(), arrivalLocation.getLon());
        LatLng departurePoint = new LatLng(departureLocation.getLat(), departureLocation.getLon());

        mMap.addMarker(new MarkerOptions().position(arrivalPoint).title(mMapPresenter.getIataCode(arrivalCity)));
        mMap.addMarker(new MarkerOptions().position(departurePoint).title(mMapPresenter.getIataCode(departureCity)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mMapPresenter.getMiddlePoint(departureLocation, arrivalLocation)));


        mMap.addPolyline(new PolylineOptions()
                .add(departurePoint, arrivalPoint)
                .width(MapConfig.MAP_INITIAL_STROKE_WIDTH_PX)
                .color(Color.BLUE)
                .geodesic(true));


        // построить ломанную линию
        // прописать тип этой линии (рунктирная)
        // и запустить анимацию движения самолетика
        // чисто внешне поменять иконки маркеров


    }


    @Override
    public void showErrorMessage(String errorMessage) {
        ToastHelper.showToastMessage(this, errorMessage);
    }
}
