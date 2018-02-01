package ru.fargus.testapp.helpers;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by admin on 01.02.18.
 */

public class ToastHelper {

    private static void make(Context context, boolean isLong, String text) {
        Toast.makeText(context, text, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    public static void showToastMessage(Context context, String text) {
        make(context, true, text);
    }
}
