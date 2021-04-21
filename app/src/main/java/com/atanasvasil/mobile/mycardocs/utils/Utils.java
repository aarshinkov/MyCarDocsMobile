package com.atanasvasil.mobile.mycardocs.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import static com.atanasvasil.mobile.mycardocs.utils.AppConstants.SHARED_PREF_AUTH;
import static com.atanasvasil.mobile.mycardocs.utils.AppConstants.SHARED_PREF_USER_EMAIL;
import static com.atanasvasil.mobile.mycardocs.utils.AppConstants.SHARED_PREF_USER_FIRST_NAME;
import static com.atanasvasil.mobile.mycardocs.utils.AppConstants.SHARED_PREF_USER_ID;
import static com.atanasvasil.mobile.mycardocs.utils.AppConstants.SHARED_PREF_USER_LAST_NAME;

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

    public static <T extends Number> String getWholeNumberFormatted(T number) {

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.getDefault());
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

        symbols.setGroupingSeparator(' ');
        formatter.setDecimalFormatSymbols(symbols);

        return formatter.format(number);
    }

    public static Boolean isLoggedIn(SharedPreferences pref) {

        LoggedUser loggedUser = getLoggedUser(pref);

        if (loggedUser == null) {
            return false;
        }

        return loggedUser.getUserId() != null;
    }

    public static LoggedUser getLoggedUser(SharedPreferences pref) {

        if (pref.getString(SHARED_PREF_USER_ID, null) == null || pref.getString(SHARED_PREF_AUTH, null) == null) {
            return null;
        }

        LoggedUser loggedUser = new LoggedUser();
        loggedUser.setAuthorization(pref.getString(SHARED_PREF_AUTH, null));
        loggedUser.setUserId(pref.getString(SHARED_PREF_USER_ID, null));
        loggedUser.setEmail(pref.getString(SHARED_PREF_USER_EMAIL, null));
        loggedUser.setFirstName(pref.getString(SHARED_PREF_USER_FIRST_NAME, null));
        loggedUser.setLastName(pref.getString(SHARED_PREF_USER_LAST_NAME, null));

        return loggedUser;
    }
}
