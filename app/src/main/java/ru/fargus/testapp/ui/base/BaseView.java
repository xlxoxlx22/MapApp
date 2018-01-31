package ru.fargus.testapp.ui.base;

import java.util.List;

import ru.fargus.testapp.model.City;

/**
 * Created by Дмитрий on 01.02.2018.
 */

public interface BaseView {
    void updateCitiesList(List<City> cities);
    void showErrorMessage(String errorMessage);
}
