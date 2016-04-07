package com.krepchenko.yourshoppinglist.utils;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Ann on 16.02.2016.
 */
public class TextUtils {


    private static final String patternStartSpecSymbol = "^(\\p{L}{1}).*$";


    public static boolean checkNameForStartSpecSymbols(String name){
        return name.matches(patternStartSpecSymbol);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}
