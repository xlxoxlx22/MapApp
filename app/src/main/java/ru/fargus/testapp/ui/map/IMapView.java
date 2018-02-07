package ru.fargus.testapp.ui.map;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import ru.fargus.testapp.ui.base.BaseView;

/**
 * Created by Дмитрий on 31.01.2018.
 */

public interface IMapView extends BaseView {

    void buildRouteOnMap(List<LatLng> points);
}
