package com.lweynant.yearly.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;


import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.lweynant.yearly.AlarmGenerator;
import com.lweynant.yearly.R;
import com.lweynant.yearly.YearlyApp;
import com.lweynant.yearly.model.Birthday;
import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.model.EventRepoSerializer;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.NotificationTime;
import com.lweynant.yearly.util.Clock;
import com.lweynant.yearly.util.EventRepoSerializerToFileDecorator;
import com.lweynant.yearly.util.UUID;

import org.joda.time.LocalDate;


import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class EventsActivity extends AppCompatActivity {

    private FloatingActionsMenu menuMultipleActions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate");
        setContentView(R.layout.activity_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        final FloatingActionButton actionA = (FloatingActionButton) findViewById(R.id.action_add_event);
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YearlyApp app = (YearlyApp) getApplication();
                EventRepo repo = app.getRepo();
                LocalDate date = LocalDate.now();
                repo.add(new Birthday("Darth Vader", date.getMonthOfYear(), date.getDayOfMonth(), new Clock(), new UUID()));
                Snackbar.make(view, getResources().getString(R.string.adding_events_not_supported), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                //actionA.setTitle("Action A clicked");
                if(menuMultipleActions.isExpanded()){
                    menuMultipleActions.collapse();
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.action_add_birthday);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddBirthdayActivity.class);
                startActivity(intent);
                menuMultipleActions.collapse();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Timber.d("onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_events, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Timber.d("onOptionsItemSelected");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else {
            YearlyApp application = (YearlyApp) getApplication();
            if (id == R.id.action_archive){
                Timber.i("archive");
                EventRepo repo = application.getRepo();
                Observable<IEvent> events = repo.getEvents();
                events.subscribeOn(Schedulers.io())
                        .subscribe(new EventRepoSerializerToFileDecorator(application.getRepoAccessor(), new EventRepoSerializer(new Clock())));
            }
            else if (id == R.id.action_set_alarm){
                Timber.i("set alarm");
                YearlyApp app = application;
                LocalDate now = LocalDate.now();
                Observable<NotificationTime> nextAlarmObservable = app.getRepo().notificationTimeForFirstUpcomingEvent(now);
                nextAlarmObservable.subscribeOn(Schedulers.io())
                                    .subscribe(new AlarmGenerator(this));
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
