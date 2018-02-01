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

public class SearchFragment extends Fragment implements SearchView{

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
        mViewsUnbinder.unbind();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSearchPresenter.detachView();
    }


    private void setupUIListeners(){
        mSearchPresenter.addDisposable(
                RxTextView.textChanges(mDepartureInput)
                        .debounce(250, TimeUnit.MILLISECONDS)
                        .map(charSequence -> charSequence.toString().trim())
                        .filter(inputText -> inputText.length() > 0)
                        .subscribe(inputText -> mSearchPresenter.obtainCitiesList(inputText))

        );

        mSearchPresenter.addDisposable(
                RxTextView.textChanges(mArrivalInput)
                        .debounce(250, TimeUnit.MILLISECONDS)
                        .filter(charSequence -> charSequence.length() > 2)
                        .map(charSequence -> charSequence.toString().trim())
                        .subscribe(inputText -> mSearchPresenter.obtainCitiesList(inputText))
        );

        mSearchPresenter.addDisposable(
                RxView.clicks(mFindFlightsButton)
                        .debounce(250, TimeUnit.MILLISECONDS)
                        .subscribe(o -> mSearchPresenter.findFlight())
        );
    }

    public void setupAdapters() {
        mDepartureAdapter = new CitiesAdapter(getActivity(), R.layout.list_item_city, new ArrayList<City>());
        mDepartureInput.setAdapter(mDepartureAdapter);

        mArrivalAdapter = new CitiesAdapter(getActivity(), R.layout.list_item_city, new ArrayList<City>());
        mArrivalInput.setAdapter(mArrivalAdapter);
    }



    @Override
    public void updateCitiesList(List<City> cities) {
        if (mDepartureAdapter == null) {
            mDepartureAdapter = new CitiesAdapter(getActivity(), R.layout.list_item_city, cities);
            mDepartureInput.setAdapter(mDepartureAdapter);
        } else {
            mDepartureAdapter.clear();
            mDepartureAdapter.addAll(cities);
        }
        mDepartureAdapter.notifyDataSetChanged();

    }

    @Override
    public void showErrorMessage(String errorMessage) {
        ToastHelper.showToastMessage(getActivity(), errorMessage);
    }

    @Override
    public void openMapActivity() {
        MapActivity.startActivity(getActivity());
    }
}
