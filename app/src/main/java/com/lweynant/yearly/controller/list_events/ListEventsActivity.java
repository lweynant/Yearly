package com.lweynant.yearly.controller.list_events;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.lweynant.yearly.BaseYearlyAppComponent;
import com.lweynant.yearly.EventRepoSerializerToFileDecorator;
import com.lweynant.yearly.R;
import com.lweynant.yearly.controller.AlarmGenerator;
import com.lweynant.yearly.controller.BaseActivity;
import com.lweynant.yearly.controller.BaseFragment;
import com.lweynant.yearly.controller.SingleFragmentActivity;
import com.lweynant.yearly.controller.add_event.AddBirthdayActivity;
import com.lweynant.yearly.controller.add_event.AddBirthdayContract;
import com.lweynant.yearly.controller.add_event.AddEventActivity;
import com.lweynant.yearly.controller.add_event.AddEventContract;
import com.lweynant.yearly.model.EventRepoSerializer;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.IEventRepo;
import com.lweynant.yearly.model.ITransaction;
import com.lweynant.yearly.model.NotificationTime;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IJsonFileAccessor;
import com.lweynant.yearly.platform.IUniqueIdGenerator;
import com.lweynant.yearly.ui.IEventViewFactory;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import timber.log.Timber;


public class ListEventsActivity extends SingleFragmentActivity implements ListEventsContract.ActivityView {

    private static final int REQUEST_ADD_BIRTHDAY = 1;
    private static final int REQUEST_ADD_EVENT = 2;
    private static final int REQUEST_EDIT_EVENT = 3;
    @Inject IClock clock;
    @Inject IUniqueIdGenerator idGenerator;
    @Inject IEventRepo repo;
    @Inject ITransaction transaction;
    @Inject IJsonFileAccessor fileAccessor;
    @Inject AlarmGenerator alarmGenerator;
    @Inject IEventViewFactory eventViewFactory;
    @Inject ListEventsContract.UserActionsListener userActionsListener;
    @Bind(R.id.multiple_actions) FloatingActionsMenu menuMultipleActions;
    @Bind(R.id.action_add_event) FloatingActionButton addEventButton;
    @Bind(R.id.action_add_birthday) FloatingActionButton addBirthdayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate");
        Timber.d("injected component");
        setContentView(R.layout.activity_list_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        userActionsListener.setActivityView(this);

        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userActionsListener.addNewEvent();

                menuMultipleActions.collapse();
            }
        });

        addBirthdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userActionsListener.addNewBirthday();

                menuMultipleActions.collapse();
            }
        });
    }

    @Override protected BaseFragment createFragment() {
        return ListEventsActivityFragment.newInstance();
    }

    @Override
    protected void injectDependencies(BaseYearlyAppComponent component) {
        component.inject(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Timber.d("onActivityResult %d", resultCode);
        if (resultCode == Activity.RESULT_CANCELED) {
            Timber.d("nothing added");
        }
        else if (requestCode == REQUEST_ADD_BIRTHDAY) {
            String name = getNameFromIntent(data);
            if (name != null) {
                Timber.d("adding birthday %s", name);
                View view = findViewById(R.id.multiple_actions);
                Snackbar.make(view, String.format(getResources().getString(R.string.add_birthday_for), name), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            } else {
                Timber.d("nothing added");
            }
        }
        else if (requestCode == REQUEST_ADD_EVENT) {
            String name = getNameFromIntent(data);
            if (name!= null) {
                Timber.d("adding event %s", name);
                View view = findViewById(R.id.multiple_actions);
                Snackbar.make(view, String.format(getResources().getString(R.string.add_event_for), name), Snackbar.LENGTH_LONG)
                      .setAction("Action", null).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getNameFromIntent(Intent data) {
        Bundle bundle = data.getBundleExtra(IEvent.EXTRA_KEY_EVENT);
        return data.getStringExtra(IEvent.KEY_NAME);
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

        return super.onOptionsItemSelected(item);
    }


    //view interface
    @Override public void showEventDetailsUI(IEvent event) {
        Timber.d("showEventDetailsUI for %s", event.toString());
        Bundle bundle = new Bundle();
        event.archiveTo(bundle);
        Intent intent = eventViewFactory.getActivityIntentForShowingEvent(this, bundle);
        startActivityForResult(intent, REQUEST_EDIT_EVENT);
    }

    @Override public void showAddNewBirthdayUI() {
        Intent intent = new Intent(ListEventsActivity.this, AddBirthdayActivity.class);
        Timber.d("startActivityForResult");
        startActivityForResult(intent, REQUEST_ADD_BIRTHDAY);
    }

    @Override public void showAddNewEventUI() {
        Timber.d("showAddNewEventUI");
        Intent intent = new Intent(ListEventsActivity.this, AddEventActivity.class);
        startActivityForResult(intent, REQUEST_ADD_EVENT);
    }
}
