package ru.fargus.testapp.model;

import com.google.gson.annotations.SerializedName;

import ru.fargus.testapp.ui.search.constants.SearchType;

/**
 * Created by Дмитрий on 01.02.2018.
 */

public class RequestObject {

    private final String REQUEST_OBJECT_LANGUAGE = "ru";

    @SerializedName("term")
    String mTerm;

    @SerializedName("lang")
    String mLanguage;

    SearchType mType;

    public RequestObject(String term, SearchType type) {
        this.mTerm = term;
        this.mType = type;
        this.mLanguage = REQUEST_OBJECT_LANGUAGE;
    }

    public String getTerm() {return mTerm;}
    public String getLanguage() {return mLanguage;}
    public SearchType getSearchType() {return mType;}
}
