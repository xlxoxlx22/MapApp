package ru.fargus.testapp.ui.map.util;

import android.animation.TypeEvaluator;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Дмитрий on 07.02.2018.
 */

public class LatLngEvaluator implements TypeEvaluator<LatLng> {



    @Override
    public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
        return new LatLng(endValue.latitude, endValue.longitude);
    }
}