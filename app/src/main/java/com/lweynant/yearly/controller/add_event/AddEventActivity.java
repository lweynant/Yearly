package com.lweynant.yearly.controller.add_event;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.lweynant.yearly.BaseYearlyAppComponent;
import com.lweynant.yearly.R;
import com.lweynant.yearly.controller.BaseActivity;
import com.lweynant.yearly.controller.SingleFragmentActivity;

import javax.inject.Inject;

import timber.log.Timber;

public class AddEventActivity extends SingleFragmentActivity {

    public static final String EXTRA_INITIAL_EVENT_BUNDLE = "AddEventActivity.initial.event";
    @Inject AddEventContract.UserActionListener userActionListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override protected Fragment createFragment() {
        Bundle args = new Bundle();
        Intent intent = getIntent();
        if (intent.hasExtra(AddEventActivity.EXTRA_INITIAL_EVENT_BUNDLE)) {
            args = intent.getBundleExtra(AddEventActivity.EXTRA_INITIAL_EVENT_BUNDLE);
        }
        else {
            args = new Bundle();
        }
        return AddEventActivityFragment.newInstance(args);
    }

    @Override protected void injectDependencies(BaseYearlyAppComponent component) {
        component.inject(this);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        Timber.d("onOptionsItemSelected");
        if (item.getItemId() == android.R.id.home) {
            Timber.d("item id is android.R.id.home");
            userActionListener.saveEvent();
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override public void onBackPressed() {
        Timber.d("onBackPressed");
        userActionListener.saveEvent();
        super.onBackPressed();
    }


}
