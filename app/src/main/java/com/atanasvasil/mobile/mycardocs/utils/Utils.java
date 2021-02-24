package com.atanasvasil.mobile.mycardocs.utils;

import android.content.Context;

public class Utils {

    public static String getStringResource(Context context, String resource) {

        try {
            int resId = context.getResources().getIdentifier(resource, "string", context.getPackageName());

            return context.getString(resId);

        } catch (Exception ignored) {
//            Log.e("ERROR", "Some error: ", e);
        }
        return "";
    }
}
