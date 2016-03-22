package com.lweynant.yearly.controller.add_event;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.lweynant.yearly.BaseYearlyAppComponent;
import com.lweynant.yearly.R;
import com.lweynant.yearly.controller.BaseFragment;
import com.lweynant.yearly.controller.SingleFragmentActivity;
import com.lweynant.yearly.model.IEvent;

public class AddEventActivity extends SingleFragmentActivity {

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

    @Override protected BaseFragment createFragment() {
        Bundle args = new Bundle();
        Intent intent = getIntent();
        if (intent.hasExtra(IEvent.EXTRA_KEY_EVENT)) {
            args = intent.getBundleExtra(IEvent.EXTRA_KEY_EVENT);
        }
        else {
            args = new Bundle();
        }
        return AddEventActivityFragment.newInstance(args);
    }

    @Override protected void injectDependencies(BaseYearlyAppComponent component) {
    }


}
