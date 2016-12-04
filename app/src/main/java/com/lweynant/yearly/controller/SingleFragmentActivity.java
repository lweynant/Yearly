package com.lweynant.yearly.controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.lweynant.yearly.R;

import timber.log.Timber;

public abstract class SingleFragmentActivity extends BaseActivity {
    protected  BaseFragment fragment;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFragment();
    }

    private void initFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentById(R.id.fragment) == null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            fragment = createFragment();
            transaction.add(R.id.fragment, fragment);
            transaction.commit();
        }
    }

    protected abstract BaseFragment createFragment();

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        Timber.d("onOptionsItemSelected");
        if (item.getItemId() == android.R.id.home) {
            Timber.d("item id is android.R.id.home");
            if (fragment != null) {
                fragment.onOptionsItemHomePressed();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onBackPressed() {
        Timber.d("onBackPressed");
        if (fragment != null) {
            fragment.onBackPressed();
        }
        super.onBackPressed();
    }

}
