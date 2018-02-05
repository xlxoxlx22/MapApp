package ru.fargus.testapp.ui.search;

import android.os.Bundle;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.fargus.testapp.model.City;
import ru.fargus.testapp.model.RequestObject;
import ru.fargus.testapp.model.RequestResponse;
import ru.fargus.testapp.network.ApiService;
import ru.fargus.testapp.network.RetrofitClient;
import ru.fargus.testapp.ui.base.BasePresenter;
import ru.fargus.testapp.ui.search.constants.SearchType;

/**
 * Created by Дмитрий on 31.01.2018.
 */

public class SearchPresenter<T extends SearchView> implements BasePresenter<T> {

    private T mView;
    private ApiService mApiService;
    private CompositeDisposable mCompositeDisposable;
    private Bundle mExtraParamsBundle = new Bundle();

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


    public void loadCities(String inputText, SearchType searchType) {
        RequestObject citiesRequest = new RequestObject(inputText, searchType);

        Observable.just(citiesRequest)
                .filter(requestObject -> requestObject.getTerm().length() > 0)
                .flatMap(requestObject -> loadCitiesList(requestObject))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (mView != null) {
                        if (response != null) {
                            mView.updateCitiesList(filterCitiesList(response.getCities(), inputText), searchType);
                        } else {
                            mView.showToastMessage("response is null");
                        }
                    }
                }, throwable -> mView.showToastMessage(throwable.getLocalizedMessage()));
    }



    private List<City> filterCitiesList(List<City> loadedList, String searchTerms) {
        List<City> filteredList = new ArrayList<>();

        loadedList.forEach(city -> {
            if (city.getFullname().toLowerCase().contains(searchTerms.toLowerCase())) {
                filteredList.add(city);
            }
        });
        return filteredList;
    }


    private Observable<RequestResponse> loadCitiesList(RequestObject request){
        return mApiService.getCitiesList(request.getTerm(), request.getLanguage());
    }

    public void setSelectedCityForKey(String key, City value) {
        mExtraParamsBundle.putParcelable(key, Parcels.wrap(value));
    }

    public Bundle getExtraParamsBundle(){
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
