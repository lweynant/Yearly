package com.lweynant.yearly.controller.add_event;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.lweynant.yearly.BaseYearlyAppComponent;
import com.lweynant.yearly.R;
import com.lweynant.yearly.controller.BaseFragment;
import com.lweynant.yearly.controller.SingleFragmentActivity;
import com.lweynant.yearly.model.IEvent;

public class AddBirthdayActivity extends SingleFragmentActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_birthday);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new FragmentView.OnClickListener() {
//            @Override
//            public void onClick(FragmentView view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setResult(RESULT_OK, new Intent());
    }

    @Override protected BaseFragment createFragment() {
        Intent intent = getIntent();
        Bundle args;
        if(intent.hasExtra(IEvent.EXTRA_KEY_EVENT)){
            args = intent.getBundleExtra(IEvent.EXTRA_KEY_EVENT);
        }
        else {
            args = new Bundle();
        }
        return AddBirthdayActivityFragment.newInstance(args);
    }

    @Override protected void injectDependencies(BaseYearlyAppComponent component) {
        //nothing to inject
    }


}
