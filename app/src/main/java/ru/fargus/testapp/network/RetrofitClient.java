package ru.fargus.testapp.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Дмитрий on 31.01.2018.
 */

public class RetrofitClient {

    private static ApiService api;
    private final String BASE_URL = "https://yasen.hotellook.com/";
    private static RetrofitClient ourInstance = new RetrofitClient();


    public static RetrofitClient getInstance() {
        return ourInstance;
    }

    private RetrofitClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();


        api = retrofit.create(ApiService.class);
    }

    public ApiService getApi() {
        return api;
    }


}
