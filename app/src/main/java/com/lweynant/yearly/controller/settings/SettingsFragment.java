package com.lweynant.yearly.controller.settings;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.lweynant.yearly.BuildConfig;
import com.lweynant.yearly.EventNotificationService;
import com.lweynant.yearly.R;
import com.lweynant.yearly.AlarmArchiver;
import com.lweynant.yearly.controller.archive.ArchiveActivity;
import com.lweynant.yearly.controller.archive.RestoreActivity;
import com.lweynant.yearly.platform.EventNotification;

import dagger.Provides;
import timber.log.Timber;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Callback callback;

    public interface Callback {
        public void onSetAlarmClicked();
    }
    public static final String COM_LWEYNANT_APP_VERSION = "com.lweynant.app_version";
    public static final String COM_LWEYNANT_BACKUP_ARCHIVE ="com.lweynant.backup_archive";
    public static final String COM_LWEYNANT_BACKUP_RESTORE ="com.lweynant.backup_restore";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        Preference archive = findPreference(COM_LWEYNANT_BACKUP_ARCHIVE);
        archive.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getActivity(), ArchiveActivity.class));
                return true;
            }
        });
        Preference restore = findPreference(COM_LWEYNANT_BACKUP_RESTORE);
        restore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getActivity(), RestoreActivity.class));
                return true;
            }
        });
        Preference alarm = findPreference(AlarmArchiver.CURRENT_ALARM);
        alarm.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override public boolean onPreferenceClick(Preference preference) {
                if (callback != null) callback.onSetAlarmClicked();
                return true;
            }
        });
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Callback){
            this.callback = (Callback)context;
        }
        else {
            this.callback = null;
        }
    }

    @Override public void onDetach() {
        super.onDetach();
        this.callback = null;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key == AlarmArchiver.CURRENT_ALARM){
            setCurrentAlarmPreferenceValue();
        }
        else if (key == AlarmArchiver.ALARM_SET_AT) {
            setAlarmSetAtPreferenceValue();
        }
        else if (key == EventNotification.LAST_NOTIFICATION){
            setLastNotificationPreferenceValue();
        }
    }

    private void setLastNotificationPreferenceValue() {
        final String key = EventNotification.LAST_NOTIFICATION;
        Preference pref = findPreference(key);
        pref.setSummary(getDefaultSharedPreferences().getString(key, ""));
    }

    private void setAlarmSetAtPreferenceValue() {
        final String key = AlarmArchiver.ALARM_SET_AT;
        Preference pref = findPreference(key);
        pref.setSummary(getDefaultSharedPreferences().getString(key, ""));
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
        setAlarmSetAtPreferenceValue();
        setLastNotificationPreferenceValue();
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
