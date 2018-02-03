package ru.fargus.testapp.ui.search.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.fargus.testapp.R;
import ru.fargus.testapp.model.City;

/**
 * Created by Дмитрий on 01.02.18.
 */

public class CitiesAdapter extends ArrayAdapter<City> {

    int resource;
    Context context;
    LayoutInflater inflater;
    List<City> tempResultItems;
    List<City> filterSuggestions;

    public CitiesAdapter(Context context, int resource, List<City> list) {
        super(context, resource, list);
        this.context = context;
        this.resource = resource;
        this.filterSuggestions = new ArrayList<>();
        this.filterSuggestions = new ArrayList<>(list);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Nullable
    @Override
    public City getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_city, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        City cityItem = getItem(position);
        viewHolder.bindItem(cityItem);

        return convertView;
    }

    public static class ViewHolder {
        @BindView(R.id.city_name_text_view) TextView mCityNameView;
        @BindView(R.id.iata_code_text_view) TextView mCityIataCodeView;

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }

        public void bindItem(City cityItem){
            mCityNameView.setText(cityItem.getFullname());

            if (cityItem.getIata().size() > 0) {
                mCityIataCodeView.setText(cityItem.getIata().get(0));
            } else {
                mCityIataCodeView.setText("");
            }
        }
    }


    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    /**
     * Custom Filter implementation for custom suggestions we provide.
     */
    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((City) resultValue).getFullname();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                filterSuggestions.clear();

                String filterString = constraint.toString().toLowerCase();
                for (City city : tempResultItems) {
                    if (city.getFullname().toLowerCase().contains(filterString)) {
                        filterSuggestions.add(city);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filterSuggestions;
                filterResults.count = filterSuggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<City> filterList = (ArrayList<City>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (City city : filterList) {
                    add(city);
                    notifyDataSetChanged();
                }
            }
        }
    };
}

