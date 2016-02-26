package com.lweynant.yearly.controller.add_event;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.lweynant.yearly.BaseYearlyAppComponent;
import com.lweynant.yearly.R;
import com.lweynant.yearly.controller.IExtendeFragmentLifeCycle;
import com.lweynant.yearly.controller.SingleFragmentActivity;

import javax.inject.Inject;

import timber.log.Timber;

public class AddBirthdayActivity extends SingleFragmentActivity{
    public static final String EXTRA_INITIAL_BIRTHDAY_BUNDLE = "AddBirthdayActivity.initial.birthday";
    private AddBirthdayActivityFragment addBirthdayActivityFragment;

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

    @Override protected Fragment createFragment() {
        Intent intent = getIntent();
        Bundle args;
        if(intent.hasExtra(AddBirthdayActivity.EXTRA_INITIAL_BIRTHDAY_BUNDLE)){
            args = intent.getBundleExtra(AddBirthdayActivity.EXTRA_INITIAL_BIRTHDAY_BUNDLE);
        }
        else {
            args = new Bundle();
        }
        addBirthdayActivityFragment = AddBirthdayActivityFragment.newInstance(args);
        return addBirthdayActivityFragment;
    }

    @Override protected void injectDependencies(BaseYearlyAppComponent component) {
        //nothing to inject
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        Timber.d("onOptionsItemSelected");
        if (item.getItemId() == android.R.id.home) {
            Timber.d("item id is android.R.id.home");
            addBirthdayActivityFragment.onOptionsItemHomePressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onBackPressed() {
        Timber.d("onBackPressed");
        addBirthdayActivityFragment.onBackPressed();
        super.onBackPressed();
    }

}
