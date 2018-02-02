package ru.fargus.testapp.ui.search;

import java.util.List;

import ru.fargus.testapp.model.City;
import ru.fargus.testapp.model.SearchType;
import ru.fargus.testapp.ui.base.BaseView;
import ru.fargus.testapp.ui.search.adapter.CitiesAdapter;

/**
 * Created by Дмитрий on 31.01.2018.
 */

public interface SearchView extends BaseView {

    void openMapActivity();
    CitiesAdapter getAdapterForSearchType(SearchType searchType);
    void updateCitiesList(List<City> cities, SearchType searchType);
}
