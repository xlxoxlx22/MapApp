package ru.fargus.testapp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Дмитрий on 01.02.2018.
 */

public class RequestObject {

    private final String REQUEST_OBJECT_LANGUAGE = "ru";

    @SerializedName("term")
    String mTerm;

    @SerializedName("lang")
    String mLanguage;

    public RequestObject(String term) {
        this.mTerm = term;
        this.mLanguage = REQUEST_OBJECT_LANGUAGE;
    }

    public String getTerm() {return mTerm;}
    public String getLanguage() {return mLanguage;}
}
