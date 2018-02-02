package ru.fargus.testapp.model;

/**
 * Created by Дмитрий on 31.01.2018.
 */

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;
import org.parceler.ParcelFactory;

@Parcel
public class City {

    @SerializedName("countryCode")
    @Expose
    private String countryCode;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("latinFullName")
    @Expose
    private String latinFullName;
    @SerializedName("fullname")
    @Expose
    private String fullname;
    @SerializedName("clar")
    @Expose
    private String clar;
    @SerializedName("latinClar")
    @Expose
    private String latinClar;
    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("hotelsCount")
    @Expose
    private Integer hotelsCount;
    @SerializedName("iata")
    @Expose
    private List<String> iata = null;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("latinCity")
    @Expose
    private String latinCity;
    @SerializedName("timezone")
    @Expose
    private String timezone;
    @SerializedName("timezonesec")
    @Expose
    private Integer timezonesec;
    @SerializedName("latinCountry")
    @Expose
    private String latinCountry;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("countryId")
    @Expose
    private Integer countryId;
    @SerializedName("_score")
    @Expose
    private Integer score;


//    @ParcelFactory
//    public static City create(City cityObject) {
//        return new City(cityObject);
//    }


//
//    public City(City cityObject) {
//        super();
//        this.countryCode = cityObject.getCountryCode();
//        this.country = cityObject.getCountry();
//        this.latinFullName = cityObject.getLatinFullName();
//        this.fullname = cityObject.getFullname();
//        this.clar = cityObject.getClar();
//        this.latinClar = cityObject.getLatinClar();
//        this.location = cityObject.getLocation();
//        this.hotelsCount = cityObject.getHotelsCount();
//        this.iata = cityObject.getIata();
//        this.city = cityObject.getCity();
//        this.latinCity = cityObject.getLatinCity();
//        this.timezone = cityObject.getTimezone();
//        this.timezonesec = cityObject.getTimezonesec();
//        this.latinCountry = cityObject.getLatinCountry();
//        this.id = cityObject.getId();
//        this.countryId = cityObject.getCountryId();
//        this.score = cityObject.getScore();
//        this.state = cityObject.getState();
//    }


    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLatinFullName() {
        return latinFullName;
    }

    public void setLatinFullName(String latinFullName) {
        this.latinFullName = latinFullName;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getClar() {
        return clar;
    }

    public void setClar(String clar) {
        this.clar = clar;
    }

    public String getLatinClar() {
        return latinClar;
    }

    public void setLatinClar(String latinClar) {
        this.latinClar = latinClar;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Integer getHotelsCount() {
        return hotelsCount;
    }

    public void setHotelsCount(Integer hotelsCount) {
        this.hotelsCount = hotelsCount;
    }

    public List<String> getIata() {
        return iata;
    }

    public void setIata(List<String> iata) {
        this.iata = iata;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLatinCity() {
        return latinCity;
    }

    public void setLatinCity(String latinCity) {
        this.latinCity = latinCity;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Integer getTimezonesec() {
        return timezonesec;
    }

    public void setTimezonesec(Integer timezonesec) {
        this.timezonesec = timezonesec;
    }

    public String getLatinCountry() {
        return latinCountry;
    }

    public void setLatinCountry(String latinCountry) {
        this.latinCountry = latinCountry;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

}

