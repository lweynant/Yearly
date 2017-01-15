package com.lweynant.yearly.controller.settings;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.lweynant.yearly.BaseYearlyAppComponent;
import com.lweynant.yearly.R;
import com.lweynant.yearly.controller.AlarmGenerator;
import com.lweynant.yearly.controller.BaseActivity;
import com.lweynant.yearly.model.IEventRepo;
import com.lweynant.yearly.model.NotificationTime;
import com.lweynant.yearly.platform.IClock;

import javax.inject.Inject;

import timber.log.Timber;


public class SettingsActivity extends BaseActivity {

    @Inject AlarmGenerator alarmGenerator;
    @Inject IEventRepo repo;
    @Inject IClock clock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @Override protected void injectDependencies(BaseYearlyAppComponent component) {
        component.inject(this);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        Timber.d("onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_set_alarm){
            Timber.i("set alarm");
            alarmGenerator.generate(repo.getEventsSubscribedOnProperScheduler(), clock.now(), NotificationTime.START_OF_DAY);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
