package ru.fargus.testapp.network;


import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.fargus.testapp.network.model.RequestResponse;

/**
 * Created by Дмитрий on 31.01.2018.
 */

public interface ApiService {

    @GET(ApiConfig.AUTOCOMPLETE_PATH)
    Observable<RequestResponse> getCitiesList(@Query(ApiConfig.PARAM_TERM) String term, @Query(ApiConfig.PARAM_LANGUAGE) String lang);

}
