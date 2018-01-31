package ru.fargus.testapp.network;


import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import ru.fargus.testapp.model.RequestObject;
import ru.fargus.testapp.model.RequestResponse;

/**
 * Created by Дмитрий on 31.01.2018.
 */

public interface ApiService {

    @GET(ApiConfig.AUTOCOMPLETE_PATH)
    Observable<RequestResponse> getCitiesList(@Query("term") String term, @Query("lang") String lang);

}
