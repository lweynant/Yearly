package com.lweynant.yearly.controller.list_events;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.lweynant.yearly.BaseYearlyAppComponent;
import com.lweynant.yearly.EventRepoSerializerToFileDecorator;
import com.lweynant.yearly.R;
import com.lweynant.yearly.controller.AlarmGenerator;
import com.lweynant.yearly.controller.BaseFragment;
import com.lweynant.yearly.controller.SingleFragmentActivity;
import com.lweynant.yearly.controller.add_event.AddBirthdayActivity;
import com.lweynant.yearly.controller.archive.ArchiveActivity;
import com.lweynant.yearly.controller.archive.RestoreActivity;
import com.lweynant.yearly.controller.settings.SettingsActivity;
import com.lweynant.yearly.model.EventRepoSerializer;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.IEventRepo;
import com.lweynant.yearly.model.NotificationTime;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IJsonFileAccessor;
import com.lweynant.yearly.ui.IEventViewFactory;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import timber.log.Timber;

public class ListBirthdaysActivity extends SingleFragmentActivity implements ListEventsContract.ActivityView {

    @Inject IEventViewFactory eventViewFactory;
    @Inject ListEventsContract.UserActionsListener userActionListener;
    @Bind(R.id.fab_add_birthday) FloatingActionButton fab;
    @Inject IEventRepo repo;
    @Inject IJsonFileAccessor fileAccessor;
    @Inject IClock clock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.d("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_birthdays);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        userActionListener.setActivityView(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                userActionListener.addNewBirthday();
            }
        });
    }

    @Override protected void injectDependencies(BaseYearlyAppComponent component) {
        component.inject(this);
    }

    @Override protected BaseFragment createFragment() {
        return ListEventsActivityFragment.newInstance();
    }

    @Override public void showEventDetailsUI(IEvent event) {
        Timber.d("showEventDetailsUI for %s", event.toString());
        Bundle bundle = new Bundle();
        event.archiveTo(bundle);
        Intent intent = eventViewFactory.getActivityIntentForShowingEvent(this, bundle);
        startActivity(intent);
    }

    @Override public void showAddNewBirthdayUI() {
        showAddNewEventUI();
    }

    @Override public void showAddNewEventUI() {
        Timber.d("showAddNewEventUI");
        Intent intent = new Intent(ListBirthdaysActivity.this, AddBirthdayActivity.class);
        Timber.d("startActivityForResult");
        startActivity(intent);
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
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_archive) {
            startActivity(new Intent(this, ArchiveActivity.class));
//            Timber.i("archive");
//            Observable<IEvent> events = repo.getEventsSubscribedOnProperScheduler();
//            events.subscribe(new EventRepoSerializerToFileDecorator(fileAccessor, new EventRepoSerializer(clock)));
            return true;
        } else if (id == R.id.action_restore) {
            startActivity(new Intent(this, RestoreActivity.class));
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

}
