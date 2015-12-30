package com.lweynant.yearly.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.lweynant.yearly.R;

import timber.log.Timber;

public class AddBirthdayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_birthday);
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
        setResult(RESULT_OK, new Intent());
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Timber.d("onOptionsItemSelected");
        if (item.getItemId() == android.R.id.home) {
            Timber.d("item id is android.R.id.home");
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        Timber.d("onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Timber.d("onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Timber.d("onStop");
        super.onStop();
    }

    @Override
    public void finish() {
        Timber.d("finish");

        super.finish();
    }

    @Override
    public void onBackPressed() {
        Timber.d("onBackPressed");
        super.onBackPressed();
    }
}
