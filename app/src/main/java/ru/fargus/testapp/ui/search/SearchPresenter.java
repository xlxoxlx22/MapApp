package ru.fargus.testapp.ui.search;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.fargus.testapp.model.RequestObject;
import ru.fargus.testapp.model.RequestResponse;
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

    @SuppressWarnings("unchecked")
    public SearchPresenter(T view){
        mView = view;
        // TODO потом перейти на dagger2
        mCompositeDisposable = new CompositeDisposable();
        mApiService =  RetrofitClient.getInstance().getApi();

    }

    @SuppressWarnings("unchecked")
    @Override
    public void attachView(T mvpView) {
        mView = mvpView;
    }

    @Override
    public void detachView() {
        clearDisposables();
        mView = null;
    }


    public void addDisposable(Disposable disposable) {
        mCompositeDisposable.add(disposable);
    }

    private void clearDisposables(){
        if (!mCompositeDisposable.isDisposed())
            mCompositeDisposable.clear();
    }

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


    public void findFlight() {
        if (mView != null) {
            mView.openMapActivity();
        }
    }
}
