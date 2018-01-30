package ru.fargus.testapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Дмитрий on 31.01.2018.
 */

public class RequestResponse {

    @SerializedName("cities")
    @Expose
    private List<City> cities = null;
    @SerializedName("hotels")
    @Expose
    private List<Hotel> hotels = null;

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    public List<Hotel> getHotels() {
        return hotels;
    }

    public void setHotels(List<Hotel> hotels) {
        this.hotels = hotels;
    }

}

