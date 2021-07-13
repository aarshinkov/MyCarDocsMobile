package bg.forcar.mobile.utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.android.material.textfield.TextInputLayout;

import bg.forcar.mobile.R;
import bg.forcar.mobile.activities.policies.PolicyCreateActivity;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static bg.forcar.mobile.utils.AppConstants.SHARED_PREF_AUTH;
import static bg.forcar.mobile.utils.AppConstants.SHARED_PREF_USER_EMAIL;
import static bg.forcar.mobile.utils.AppConstants.SHARED_PREF_USER_FIRST_NAME;
import static bg.forcar.mobile.utils.AppConstants.SHARED_PREF_USER_ID;
import static bg.forcar.mobile.utils.AppConstants.SHARED_PREF_USER_LAST_NAME;

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
                return "";
        }
    }

    public static String getDayOfWeek(Context context, Integer dayOfWeek) {

        switch (dayOfWeek) {
            case 1:
                return context.getString(R.string.week_sunday);
            case 2:
                return context.getString(R.string.week_monday);
            case 3:
                return context.getString(R.string.week_tuesday);
            case 4:
                return context.getString(R.string.week_wednesday);
            case 5:
                return context.getString(R.string.week_thursday);
            case 6:
                return context.getString(R.string.week_friday);
            case 7:
                return context.getString(R.string.week_saturday);
        }

        return "";
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

    public static void setAppLocale(Resources res, String localeCode) {
        Locale locale = new Locale(localeCode);
        Locale.setDefault(locale);
        Configuration config = res.getConfiguration();
        config.setLocale(locale);
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    public static void showDateTimeDialog(final EditText field, final TextInputLayout label, final Context context) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(context.getString(R.string.date_time_1), Locale.getDefault());

                        field.setText(simpleDateFormat.format(calendar.getTime()));
                        label.setError(null);
                    }
                };

                new TimePickerDialog(context, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
            }
        };

        new DatePickerDialog(context, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    public static void showDateDialog(final EditText field, final Context context) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(context.getString(R.string.date_1), Locale.getDefault());
                field.setText(simpleDateFormat.format(calendar.getTime()));

            }
        };

        new DatePickerDialog(context, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}
