package bg.forcar.mobile.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import bg.forcar.mobile.R;

public class PreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}