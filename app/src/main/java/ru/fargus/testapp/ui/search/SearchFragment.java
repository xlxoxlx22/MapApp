package ru.fargus.testapp.ui.search;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.fargus.testapp.R;
import ru.fargus.testapp.helpers.ToastHelper;
import ru.fargus.testapp.model.City;
import ru.fargus.testapp.ui.map.MapActivity;
import ru.fargus.testapp.ui.search.adapter.CitiesAdapter;
import ru.fargus.testapp.ui.search.constants.SearchConfig;
import ru.fargus.testapp.ui.search.constants.SearchType;

public class SearchFragment extends Fragment implements ISearchView {

    private View mView;
    private Unbinder mViewsUnbinder;
    private CitiesAdapter mArrivalAdapter;
    private CitiesAdapter mDepartureAdapter;
    private SearchPresenter mSearchPresenter;

    @BindView(R.id.find_flights_button) Button mFindFlightsButton;
    @BindView(R.id.arrival_place_edit_text) AutoCompleteTextView mArrivalInput;
    @BindView(R.id.departure_place_text_view) AutoCompleteTextView mDepartureInput;


    public SearchFragment() {}

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSearchPresenter = new SearchPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_search, container, false);
        mViewsUnbinder = ButterKnife.bind(this, mView);

        setupAdapters();
        setupUIListeners();

        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSearchPresenter.detachView();
        mViewsUnbinder.unbind();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    private void setupUIListeners(){

        mSearchPresenter.addDisposable(
                RxTextView.afterTextChangeEvents(mDepartureInput)
                        .debounce(250, TimeUnit.MILLISECONDS)
                        .subscribe(event -> mSearchPresenter.loadCities(event.view().getText().toString(), SearchType.SEARCH_TYPE_DEPARTURE))
        );

        mSearchPresenter.addDisposable(
                RxTextView.afterTextChangeEvents(mArrivalInput)
                        .debounce(250, TimeUnit.MILLISECONDS)
                        .subscribe(event -> mSearchPresenter.loadCities(event.view().getText().toString(), SearchType.SEARCH_TYPE_ARRIVAL))
        );


        mSearchPresenter.addDisposable(
                RxView.clicks(mFindFlightsButton)
                        .debounce(250, TimeUnit.MILLISECONDS)
                        .subscribe(event -> mSearchPresenter.findFlight())
        );
    }

    public void setupAdapters() {
        mDepartureAdapter = new CitiesAdapter(getActivity(), R.layout.list_item_city, new ArrayList<City>());
        mDepartureInput.setAdapter(mDepartureAdapter);
        mDepartureInput.setOnItemClickListener((adapterView, view, position, id) -> {
            City currentCity = (City) adapterView.getItemAtPosition(position);
            mSearchPresenter.setSelectedCityForKey(SearchConfig.SEARCH_DEPARTURE_PARAM, currentCity);
        });

        mArrivalAdapter = new CitiesAdapter(getActivity(), R.layout.list_item_city, new ArrayList<City>());
        mArrivalInput.setAdapter(mArrivalAdapter);
        mArrivalInput.setOnItemClickListener((adapterView, view, position, id) -> {
            City currentCity = (City) adapterView.getItemAtPosition(position);
            mSearchPresenter.setSelectedCityForKey(SearchConfig.SEARCH_ARRIVAL_PARAM, currentCity);
        });
    }



    @Override
    public void updateCitiesList(List<City> cities, SearchType type) {

        CitiesAdapter adapter = getAdapterForSearchType(type);
        if (adapter != null) {
            adapter.clear();
            adapter.addAll(cities);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showToastMessage(final String errorMessage) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> ToastHelper.showToastMessage(getActivity(), errorMessage));
        }

    }

    @Override
    public void openMapActivity() {
        MapActivity.buildIntent(getActivity(), mSearchPresenter.getExtraParamsBundle());
    }

    @Override
    public CitiesAdapter getAdapterForSearchType(SearchType searchType) {
        CitiesAdapter adapter;
        switch (searchType) {
            case SEARCH_TYPE_ARRIVAL:
                adapter = mArrivalAdapter;
                break;
            case SEARCH_TYPE_DEPARTURE:
                adapter = mDepartureAdapter;
                break;
            default:
                adapter = new CitiesAdapter(getActivity(), R.layout.list_item_city, new ArrayList<City>());
                break;
        }
        return adapter;
    }
}
