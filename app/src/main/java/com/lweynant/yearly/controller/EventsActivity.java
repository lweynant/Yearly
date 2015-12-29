package com.lweynant.yearly.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.lweynant.yearly.AlarmGenerator;
import com.lweynant.yearly.R;
import com.lweynant.yearly.YearlyApp;
import com.lweynant.yearly.model.Birthday;
import com.lweynant.yearly.model.BirthdayBuilder;
import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.model.EventRepoSerializer;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.IJsonFileAccessor;
import com.lweynant.yearly.model.NotificationTime;
import com.lweynant.yearly.util.EventRepoSerializerToFileDecorator;
import com.lweynant.yearly.util.IClock;
import com.lweynant.yearly.util.IUniqueIdGenerator;

import org.joda.time.LocalDate;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;



public class EventsActivity extends BaseActivity {

    private FloatingActionsMenu menuMultipleActions;
    @Inject
    IClock clock;
    @Inject
    IUniqueIdGenerator idGenerator;
    @Inject
    EventRepo repo;
    @Inject
    IJsonFileAccessor fileAccessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate");
        getComponent().inject(this);
        Timber.d("injected component");
        setContentView(R.layout.activity_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        final FloatingActionButton actionA = (FloatingActionButton) findViewById(R.id.action_add_event);
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalDate date = LocalDate.now();
                //noinspection ResourceType
                repo.add(new Birthday("Darth","Vader", date.getMonthOfYear(), date.getDayOfMonth(), clock, idGenerator));
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
                Intent intent = new Intent(EventsActivity.this, AddBirthdayActivity.class);
                Timber.d("startActivityForResult");
                startActivityForResult(intent, 0);
                menuMultipleActions.collapse();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Timber.d("onActivityResult %d", resultCode);
        if (data != null) {
            Timber.d("inspecting the valid intent");
            BirthdayBuilder builder = new BirthdayBuilder(clock, idGenerator);
            Bundle bundle = data.getBundleExtra(AddBirthdayActivityFragment.EXTRA_KEY_BIRTHDAY);
            if (bundle != null) {
                Timber.d("we have a bundle");
                builder.set(bundle);
                Birthday bd = builder.build();
                if (bd != null) {
                    Timber.d("adding birthday %s", bd);
                    repo.add(bd);
                    View view = findViewById(R.id.multiple_actions);
                    Snackbar.make(view, String.format(getResources().getString(R.string.add_birthday_for), bd.getName()), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Timber.d("nothing added");
                }
            }
        }
        else{
            Timber.d("onActivityResult with null data...");
        }

        super.onActivityResult(requestCode, resultCode, data);
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
                Observable<IEvent> events = repo.getEvents();
                events.subscribeOn(Schedulers.io())
                        .subscribe(new EventRepoSerializerToFileDecorator(fileAccessor, new EventRepoSerializer(clock)));
            }
            else if (id == R.id.action_set_alarm){
                Timber.i("set alarm");
                LocalDate now = LocalDate.now();
                Observable<NotificationTime> nextAlarmObservable = repo.notificationTimeForFirstUpcomingEvent(now);
                nextAlarmObservable.subscribeOn(Schedulers.io())
                                    .subscribe(new AlarmGenerator(this));
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
