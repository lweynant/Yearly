package com.lweynant.yearly.utils;

public class CaseFormat {
    public static String capitalizeFirstLetter(String s){
        if (s!=null && s.length() > 0) {
            return s.substring(0, 1).toUpperCase() + s.substring(1);
        }
        else
            return s;
    }

    public static String uncapitalizeFirstLetter(String s) {
        if (s!=null && s.length() > 0) {
            return s.substring(0, 1).toLowerCase() + s.substring(1);
        }
        else
            return s;
    }
}
