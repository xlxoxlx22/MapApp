package ru.fargus.testapp.ui.search;

import android.os.Bundle;
import android.os.Parcelable;

import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.fargus.testapp.model.City;
import ru.fargus.testapp.model.RequestObject;
import ru.fargus.testapp.model.RequestResponse;
import ru.fargus.testapp.model.SearchType;
import ru.fargus.testapp.network.ApiService;
import ru.fargus.testapp.network.RetrofitClient;
import ru.fargus.testapp.ui.base.BasePresenter;
import ru.fargus.testapp.ui.base.BaseView;

/**
 * Created by Дмитрий on 31.01.2018.
 */

public class SearchPresenter<T extends SearchView> implements BasePresenter<T> {

    private T mView;
    private ApiService mApiService;
    private CompositeDisposable mCompositeDisposable;
    private Bundle mExtraParamsBundle = new Bundle();
    private List<City> citiesList = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public SearchPresenter(T view){
        mView = view;
        // TODO потом перейти на dagger2
        mCompositeDisposable = new CompositeDisposable();
        mApiService =  RetrofitClient.getInstance().getApi();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void attachView(T baseView) {
        mView = baseView;
    }

    @Override
    public void detachView() {
        clearDisposables();
        mView = null;
    }

    @Override
    public void addDisposable(Disposable disposable) {
        mCompositeDisposable.add(disposable);
    }
    private void clearDisposables(){
        if (!mCompositeDisposable.isDisposed())
            mCompositeDisposable.clear();
    }


    public void obtainCities(String inputText, SearchType searchType) {
        RequestObject citiesRequest = new RequestObject(inputText, searchType);

        Observable.just(citiesRequest)
                .filter(requestObject -> requestObject.getTerm().length() > 0)
                .flatMap(requestObject -> {
                    if (citiesList.size() != 0){
                        return filterCitiesList(requestObject.getTerm());
                    } else {
                        return loadCitiesList(requestObject);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (mView != null) {
                        if (response != null) {
                            citiesList = response.getCities();
                            mView.updateCitiesList(citiesList, searchType);
                        } else {
                            mView.showErrorMessage("response is null");
                        }
                    }
                }, throwable -> mView.showErrorMessage(throwable.getLocalizedMessage()));
    }



    private Observable<RequestResponse> filterCitiesList(String filterString) {
        List<City> filteredList = new ArrayList<>();

        citiesList.forEach(city -> {
            if (!city.getFullname().toLowerCase().contains(filterString.toLowerCase())) {
                filteredList.add(city);
            }
        });

        RequestResponse response = new RequestResponse();
        response.setCities(filteredList);
        return Observable.just(response);
    }


    private Observable<RequestResponse> loadCitiesList(RequestObject citiesRequest){
        return mApiService.getCitiesList(citiesRequest.getTerm(), citiesRequest.getLanguage());
    }

    public void setSelectedCityForKey(String key, City value) {
        mExtraParamsBundle.putParcelable(key, Parcels.wrap(value));
    }

    public Bundle getmExtraParamsBundle(){
        if (mExtraParamsBundle != null) {
            return mExtraParamsBundle;
        } else {
            return new Bundle();
        }

    }



    public void findFlight() {
        if (mView != null) {
            mView.openMapActivity();
        }
    }
}
