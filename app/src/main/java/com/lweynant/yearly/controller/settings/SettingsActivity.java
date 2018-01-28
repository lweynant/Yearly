package com.lweynant.yearly.controller.settings;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.lweynant.yearly.BaseYearlyAppComponent;
import com.lweynant.yearly.IStringResources;
import com.lweynant.yearly.R;
import com.lweynant.yearly.controller.AlarmGenerator;
import com.lweynant.yearly.controller.BaseActivity;
import com.lweynant.yearly.model.IEventRepo;
import com.lweynant.yearly.model.NotificationTime;
import com.lweynant.yearly.platform.IClock;

import javax.inject.Inject;

import timber.log.Timber;


public class SettingsActivity extends BaseActivity implements SettingsFragment.Callback {

    @Inject AlarmGenerator alarmGenerator;
    @Inject IEventRepo repo;
    @Inject IClock clock;
    @Inject IStringResources stringResources;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
        setTheme(R.style.Preferences);
    }

    @Override protected void injectDependencies(BaseYearlyAppComponent component) {
        component.inject(this);
    }


    @Override public void onSetAlarmClicked() {
        Timber.i("set alarm");
        Toast.makeText(this, stringResources.getString(R.string.settings_alarm_set), Toast.LENGTH_SHORT).show();
        alarmGenerator.generate(repo.getEventsSubscribedOnProperScheduler(), clock.now(), NotificationTime.START_OF_DAY);
    }
}
