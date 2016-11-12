package com.lweynant.yearly.controller.show_event;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.lweynant.yearly.BaseYearlyAppComponent;
import com.lweynant.yearly.R;
import com.lweynant.yearly.controller.BaseActivity;
import com.lweynant.yearly.controller.BaseFragment;
import com.lweynant.yearly.controller.DateFormatter;
import com.lweynant.yearly.controller.SingleFragmentActivity;
import com.lweynant.yearly.model.Birthday;
import com.lweynant.yearly.model.BirthdayBuilder;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IEventNotificationText;
import com.lweynant.yearly.ui.IEventViewFactory;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Years;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;


public class ShowBirthdayActivity extends SingleFragmentActivity implements ShowBirthdayFragment.Callback {

    private ShareActionProvider shareActionProvider;
    private String textToShare;

    @SuppressWarnings("ResourceType") @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.d("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_birthday);
    }

    @Override protected BaseFragment createFragment() {
        return ShowBirthdayFragment.newInstance(getBundle());
    }

    // Create and return the Share Intent
    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
        return shareIntent;
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show_birthday, menu);

        //return true to show the menu
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_item_share){
            showShareDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showShareDialog() {
        startActivity(Intent.createChooser(createShareIntent(), getString(R.string.menu_share)));
    }

    private Bundle getBundle() {
        Intent intent = getIntent();
        Bundle args;
        if(intent.hasExtra(IEvent.EXTRA_KEY_EVENT)){
            args = intent.getBundleExtra(IEvent.EXTRA_KEY_EVENT);
        }
        else {
            args = new Bundle();
        }
        return args;
    }

    @Override protected void injectDependencies(BaseYearlyAppComponent component) {
    }

    @Override public void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override public void setShareIntentText(String text) {

        textToShare = text;
    }


}
