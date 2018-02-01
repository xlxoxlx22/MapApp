package ru.fargus.testapp.ui.search;

import java.util.List;

import ru.fargus.testapp.model.City;
import ru.fargus.testapp.ui.base.BaseView;

/**
 * Created by Дмитрий on 31.01.2018.
 */

public interface SearchView extends BaseView {

    void openMapActivity();
    void updateCitiesList(List<City> cities);
}
