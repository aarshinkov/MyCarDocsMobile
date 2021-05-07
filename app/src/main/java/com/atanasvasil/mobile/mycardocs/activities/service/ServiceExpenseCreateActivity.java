package com.atanasvasil.mobile.mycardocs.activities.service;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.utils.LoggedUser;

import java.util.Locale;

import static com.atanasvasil.mobile.mycardocs.utils.AppConstants.SHARED_PREF_NAME;
import static com.atanasvasil.mobile.mycardocs.utils.Utils.getLoggedUser;

public class ServiceExpenseCreateActivity extends AppCompatActivity {

    private SharedPreferences pref;
    private LoggedUser loggedUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_expense_create);

        getSupportActionBar().setTitle(R.string.service_expense_title);

        pref = getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        loggedUser = getLoggedUser(pref);

        final String zeroFormatted = String.format(Locale.getDefault(), "%.2f", 0.00);
    }
}
