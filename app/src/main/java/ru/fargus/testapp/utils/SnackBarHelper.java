package ru.fargus.testapp.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;



public class SnackBarHelper {

    public static void showShortMessage(Snackbar snackbar, View containerView, String message) {
        showMessage(containerView, message, Snackbar.LENGTH_SHORT);
    }

    public static void showLongMessage(Snackbar snackbar, View containerView, String message) {
        showMessage(containerView, message, Snackbar.LENGTH_LONG);
    }

    public static void showSnackbarMessage(View containerView, String message) {
        showMessage(containerView, message, Snackbar.LENGTH_SHORT);
    }

    public static void showMessageWithCallback(View containerView, String header, String message, Runnable action){
        showMessageWithAction(containerView, header, message, action);
    }

    /***
     * Show message with via a lazy loaded snackbar. Note if an existing snackbar is showing it will
     * be dismissed.
     * @param containerView The view to find the parent from.
     * @param message Message to display in snackbar.
     * @param duration How long to show the snackbar for.
     */
    private static void showMessage(@NonNull View containerView, @NonNull String message, int duration) {
        Snackbar snackbar = Snackbar.make(containerView, message, duration);
        snackbar.show();
    }

    private static void showMessageWithAction(@NonNull View containerView, @NonNull String header , @NonNull String message, Runnable action){
        Snackbar snackbar = Snackbar
                .make(containerView, header, Snackbar.LENGTH_LONG)
                .setAction(message, v -> action.run());
        snackbar.show();
    }
}
