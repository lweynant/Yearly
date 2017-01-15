package com.lweynant.yearly.controller.settings;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.lweynant.yearly.R;
import com.lweynant.yearly.platform.AlarmArchiver;

import timber.log.Timber;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key == AlarmArchiver.CURRENT_ALARM){
            setCurrentAlarmPreferenceValue();
        }
    }

    public void setCurrentAlarmPreferenceValue() {
        final String key = AlarmArchiver.CURRENT_ALARM;
        Preference pref = findPreference(key);
        pref.setSummary(getDefaultSharedPreferences().getString(key, ""));
    }

    @Override public void onResume() {
        Timber.d("onResume");
        super.onResume();
        setCurrentAlarmPreferenceValue();
        getDefaultSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    public SharedPreferences getDefaultSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
    }

    @Override public void onPause() {
        Timber.d("onPause");
        super.onPause();
        getDefaultSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
