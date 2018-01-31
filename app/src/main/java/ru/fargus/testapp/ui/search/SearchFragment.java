package ru.fargus.testapp.ui.search;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.fargus.testapp.R;
import ru.fargus.testapp.model.City;
import ru.fargus.testapp.ui.base.BaseView;
import ru.fargus.testapp.utils.SnackBarHelper;

public class SearchFragment extends Fragment implements BaseView{

    private View mView;
    private Unbinder mViewsUnbinder;
    private SearchPresenter mSearchPresenter;
    private final String TAG = getClass().getSimpleName();
    @BindView(R.id.find_flights_button) Button mFindFlightsButton;
    @BindView(R.id.arrival_place_edit_text) EditText mArrivalInput;
    @BindView(R.id.departure_place_text_view)  EditText mDepartureInput;


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

        RxTextView.textChanges(mDepartureInput)
                .debounce(250, TimeUnit.MILLISECONDS)
                .filter(charSequence -> charSequence.length() > 0)
                .subscribe(inputTest -> mSearchPresenter.obtainCitiesList(inputTest.toString()));



        RxView.clicks(mFindFlightsButton).subscribe(o -> {
            // закрыывть клавиатуру нужно
            SnackBarHelper.showSnackbarMessage(mView, "Нажали на кнопку \"Найти\"");
        });
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
    }

    @Override
    public void updateCitiesList(List<City> cities) {
        Log.i(TAG, "Load city list with size = " + cities.size());
    }

    @Override
    public void showErrorMessage(String errorMessage) {
        SnackBarHelper.showSnackbarMessage(mView, errorMessage);
    }
}
