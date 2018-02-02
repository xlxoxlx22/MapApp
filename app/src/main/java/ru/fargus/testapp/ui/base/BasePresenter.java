package ru.fargus.testapp.ui.base;

import io.reactivex.disposables.Disposable;

/**
 * Created by Дмитрий on 01.02.2018.
 */

public interface BasePresenter<V extends BaseView> {

    void attachView(V baseView);

    void detachView();

    void addDisposable(Disposable disposable);

}
