package com.lweynant.yearly.controller.archive;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lweynant.yearly.BaseYearlyAppComponent;
import com.lweynant.yearly.EventRepoSerializerToFileDecorator;
import com.lweynant.yearly.IStringResources;
import com.lweynant.yearly.R;
import com.lweynant.yearly.model.EventRepoSerializer;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.IEventRepo;
import com.lweynant.yearly.platform.GoogleDriveApiClientAsyncTask;
import com.lweynant.yearly.utils.JsonStreamWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.inject.Inject;

import rx.Observable;
import rx.observables.BlockingObservable;
import timber.log.Timber;

public class RestoreActivity extends BaseGDriveApiClientActivity implements RestoreEventsAsyncTask.ICallback {

    @Inject IEventRepo repo;
    @Inject IStringResources stringResources;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate");

    }

    @Override protected void injectDependencies(BaseYearlyAppComponent component) {
        component.inject(this);
    }

    private static final int REQUEST_CODE_OPENER = 1;

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
        IntentSender intentSender = Drive.DriveApi
                .newOpenFileActivityBuilder()
                .setMimeType(getMimeTypes())
                .setActivityTitle(stringResources.getString(R.string.title_activity_restore))
                .build(getGoogleApiClient());
        try {
            startIntentSenderForResult(
                    intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            Timber.e(e, "Unable to send intent");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_OPENER:
                if (resultCode == RESULT_OK) {
                    DriveId driveId = (DriveId) data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                    Timber.d("Selected file's ID: " + driveId);
                    DriveFile file = driveId.asDriveFile();
                    new RestoreEventsAsyncTask(this, repo, this).execute(file);
                }
                else {
                    finish();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override public void restoreSucceeded() {
        showMessage(stringResources.getString(R.string.restore_activity_success));
        finish();
    }

    @Override public void restoreFailed() {
        showMessage(stringResources.getString(R.string.restore_activity_failed));
        finish();
    }
}
