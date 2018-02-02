package ru.fargus.testapp.ui.map;

import com.google.android.gms.maps.model.LatLng;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ru.fargus.testapp.model.City;
import ru.fargus.testapp.model.Location;
import ru.fargus.testapp.ui.base.BasePresenter;

/**
 * Created by Дмитрий on 31.01.2018.
 */

public class MapPresenter<T extends MapView> implements BasePresenter<T> {

    private T mView;
    private CompositeDisposable mCompositeDisposable;

    @SuppressWarnings("unchecked")
    public MapPresenter(T view){
        mView = view;
        // TODO потом перейти на dagger2
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

    private void clearDisposables(){
        if (!mCompositeDisposable.isDisposed())
            mCompositeDisposable.clear();
    }

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


        return new LatLng(middleLat, middleLon);
    }

    public String getIataCode(City city) {
        return city.getIata().get(0);
    }

    @Override
    public void addDisposable(Disposable disposable) {

    }
}
