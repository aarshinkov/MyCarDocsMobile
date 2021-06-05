package com.aarshinkov.mobile.mycardocs.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.aarshinkov.mobile.mycardocs.R;

public class PreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}