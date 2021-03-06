package com.john.networklibrary;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

class Utils {
    public static void showSnackbar(View v, String snackbarText) {
        if (v == null || snackbarText == null) {
            return;
        }
        Snackbar.make(v, snackbarText, Snackbar.LENGTH_LONG).show();
    }
}
