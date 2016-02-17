package com.krepchenko.yourshoppinglist.utils;

/**
 * Created by Ann on 16.02.2016.
 */
public class TextUtils {


    private static final String patternStartSpecSymbol = "^(\\p{L}{1}).*$";


    public static boolean checkNameForStartSpecSymbols(String name){
        return name.matches(patternStartSpecSymbol);
    }

}
