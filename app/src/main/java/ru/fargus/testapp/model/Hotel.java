package ru.fargus.testapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by Дмитрий on 31.01.2018.
 */


public class Hotel {
    @SerializedName("state")
    @Expose
    private Object state;
    @SerializedName("stars")
    @Expose
    private Integer stars;
    @SerializedName("locationFullName")
    @Expose
    private String locationFullName;
    @SerializedName("latinLocationFullName")
    @Expose
    private String latinLocationFullName;
    @SerializedName("hotelFullName")
    @Expose
    private String hotelFullName;
    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("timezone")
    @Expose
    private String timezone;
    @SerializedName("timezonesec")
    @Expose
    private Integer timezonesec;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("locationId")
    @Expose
    private Integer locationId;
    @SerializedName("photoCount")
    @Expose
    private Integer photoCount;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("latinCity")
    @Expose
    private String latinCity;
    @SerializedName("latinClar")
    @Expose
    private String latinClar;
    @SerializedName("latinCountry")
    @Expose
    private String latinCountry;
    @SerializedName("locationHotelsCount")
    @Expose
    private Integer locationHotelsCount;
    @SerializedName("rating")
    @Expose
    private Integer rating;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("distance")
    @Expose
    private Double distance;
    @SerializedName("_score")
    @Expose
    private Double score;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("latinName")
    @Expose
    private String latinName;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("photos")
    @Expose
    private List<Integer> photos = null;

    public Object getState() {
        return state;
    }

    public void setState(Object state) {
        this.state = state;
    }

    public Integer getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public String getLocationFullName() {
        return locationFullName;
    }

    public void setLocationFullName(String locationFullName) {
        this.locationFullName = locationFullName;
    }

    public String getLatinLocationFullName() {
        return latinLocationFullName;
    }

    public void setLatinLocationFullName(String latinLocationFullName) {
        this.latinLocationFullName = latinLocationFullName;
    }

    public String getHotelFullName() {
        return hotelFullName;
    }

    public void setHotelFullName(String hotelFullName) {
        this.hotelFullName = hotelFullName;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getPhotoCount() {
        return photoCount;
    }

    public void setPhotoCount(Integer photoCount) {
        this.photoCount = photoCount;
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

    public String getLatinClar() {
        return latinClar;
    }

    public void setLatinClar(String latinClar) {
        this.latinClar = latinClar;
    }

    public String getLatinCountry() {
        return latinCountry;
    }

    public void setLatinCountry(String latinCountry) {
        this.latinCountry = latinCountry;
    }

    public Integer getLocationHotelsCount() {
        return locationHotelsCount;
    }

    public void setLocationHotelsCount(Integer locationHotelsCount) {
        this.locationHotelsCount = locationHotelsCount;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatinName() {
        return latinName;
    }

    public void setLatinName(String latinName) {
        this.latinName = latinName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Integer> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Integer> photos) {
        this.photos = photos;
    }
}
