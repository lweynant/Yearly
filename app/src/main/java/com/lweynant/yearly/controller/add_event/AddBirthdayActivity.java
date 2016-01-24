package com.lweynant.yearly.controller.add_event;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.lweynant.yearly.BaseYearlyAppComponent;
import com.lweynant.yearly.R;
import com.lweynant.yearly.controller.BaseActivity;

import javax.inject.Inject;

import timber.log.Timber;

public class AddBirthdayActivity extends BaseActivity {
    @Inject AddBirthdayContract.UserActionsListener userActionsListener;

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

    @Override protected void injectDependencies(BaseYearlyAppComponent component) {
        component.inject(this);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        Timber.d("onOptionsItemSelected");
        if (item.getItemId() == android.R.id.home) {
            Timber.d("item id is android.R.id.home");
            userActionsListener.saveBirthday();
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override public void onBackPressed() {
        Timber.d("onBackPressed");
        userActionsListener.saveBirthday();
        super.onBackPressed();
    }

}
