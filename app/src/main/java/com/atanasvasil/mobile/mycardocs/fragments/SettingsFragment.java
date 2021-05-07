package com.atanasvasil.mobile.mycardocs.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.activities.StartupActivity;

import org.jetbrains.annotations.NotNull;

import static com.atanasvasil.mobile.mycardocs.utils.Utils.setAppLocale;

public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        setHasOptionsMenu(true);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        preferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                final String language = sharedPreferences.getString("language", "en");
                setAppLocale(getResources(), language);
                Intent intent = new Intent(getContext(), StartupActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                requireActivity().finish();
                startActivity(intent);
            }
        });

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.settingsContainer, new PreferencesFragment())
                .commit();

        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }
}
