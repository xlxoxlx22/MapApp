package ru.fargus.testapp.ui.search;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.fargus.testapp.model.RequestObject;
import ru.fargus.testapp.network.ApiService;
import ru.fargus.testapp.network.RetrofitClient;
import ru.fargus.testapp.ui.base.BasePresenter;
import ru.fargus.testapp.ui.base.BaseView;

/**
 * Created by Дмитрий on 31.01.2018.
 */

public class SearchPresenter<T extends BaseView> implements BasePresenter<T> {

    private T mView;
    private ApiService mApiService;
    private RetrofitClient mRetrofitClient;

    @SuppressWarnings("unchecked")
    public SearchPresenter(T view){
        mView = view;
        mApiService =  RetrofitClient.getInstance().getApi();

    }

    @SuppressWarnings("unchecked")
    @Override
    public void attachView(T mvpView) {
        mView = mvpView;
    }

    @Override
    public void detachView() {
        mView = null;
    }

    public T getView() {
        return mView;
    }

    public void startMapActivity(){}



    public void obtainCitiesList(String inputText){
        RequestObject citiesRequest = new RequestObject(inputText);

        mApiService.getCitiesList(citiesRequest.getTerm(),
                                  citiesRequest.getLanguage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(requestResponse -> {
                    if (mView != null) {
                        if (requestResponse != null) {
                            mView.updateCitiesList(requestResponse.getCities());
                        } else {
                            mView.showErrorMessage("response is null");
                        }
                    }
                }, throwable -> mView.showErrorMessage(throwable.getLocalizedMessage()));



    }
}
