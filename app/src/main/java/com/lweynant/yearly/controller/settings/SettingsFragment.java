package com.lweynant.yearly.controller.settings;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.lweynant.yearly.BuildConfig;
import com.lweynant.yearly.R;
import com.lweynant.yearly.AlarmArchiver;

import timber.log.Timber;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String COM_LWEYNANT_APP_VERSION = "com.lweynant.app_version";
    public static final String COM_LWEYNANT_APP_CODE = "com.lweynant.app_code";

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

    private void setVersion() {
        Preference pref = findPreference(COM_LWEYNANT_APP_VERSION);
        String summary =BuildConfig.VERSION_NAME + " (" + Integer.toString(BuildConfig.VERSION_CODE) +")";
        pref.setSummary(summary);
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
        setVersion();
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
