package com.atanasvasil.mobile.mycardocs.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.atanasvasil.mobile.mycardocs.R;

public class PreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}