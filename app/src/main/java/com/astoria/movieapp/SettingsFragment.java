package com.astoria.movieapp;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.astoria.movieapp.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
    }

}