package com.atanasvasil.mobile.mycardocs.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.atanasvasil.mobile.mycardocs.R;

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

    public static String getMonthName(Context context, Integer month) {

        switch (month) {
            case 1:
                return context.getString(R.string.JAN);
            case 2:
                return context.getString(R.string.FEB);
            case 3:
                return context.getString(R.string.MAR);
            case 4:
                return context.getString(R.string.APR);
            case 5:
                return context.getString(R.string.MAY);
            case 6:
                return context.getString(R.string.JUN);
            case 7:
                return context.getString(R.string.JUL);
            case 8:
                return context.getString(R.string.AUG);
            case 9:
                return context.getString(R.string.SEP);
            case 10:
                return context.getString(R.string.OCT);
            case 11:
                return context.getString(R.string.NOV);
            case 12:
                return context.getString(R.string.DEC);
            default:
                return null;
        }
    }

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
