package com.lweynant.yearly.controller.archive;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.lweynant.yearly.BaseYearlyAppComponent;
import com.lweynant.yearly.EventRepoSerializerToFileDecorator;
import com.lweynant.yearly.IStringResources;
import com.lweynant.yearly.R;
import com.lweynant.yearly.model.EventRepoSerializer;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.IEventRepo;
import com.lweynant.yearly.platform.GoogleDriveApiClientAsyncTask;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.utils.JsonStreamWriter;

import java.io.IOException;
import java.io.OutputStream;

import javax.inject.Inject;

import rx.Observable;
import rx.observables.BlockingObservable;
import timber.log.Timber;


public class ArchiveActivity extends BaseGDriveApiClientActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, ArchiveEventsAsyncTask.ICallback {

    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 7;
    private static final int REQUEST_CODE_CREATOR = 9;
    @Inject IClock clock;
    @Inject IEventRepo repo;
    @Inject IStringResources stringResources;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate");

    }

    @Override protected void injectDependencies(BaseYearlyAppComponent component) {
        component.inject(this);
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        Timber.d("onActivityResult requestCode %d resultCode %d", requestCode, resultCode);
        switch (requestCode) {
             case REQUEST_CODE_CREATOR:
                Timber.i("REQUEST_CODE_CREATOR");
                if (resultCode == RESULT_OK) {
                    DriveId driveId = (DriveId) data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                    Timber.i("File created with ID: " + driveId);
                    DriveFile file = driveId.asDriveFile();
                    new ArchiveEventsAsyncTask(this, repo, this, clock).execute(file);
                }
                finish();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override public void onConnected(@Nullable Bundle bundle) {
        Timber.d("onConnected");
        Drive.DriveApi.newDriveContents(getGoogleApiClient())
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                    @Override
                    public void onResult(DriveApi.DriveContentsResult result) {
                        Timber.d("DriveApi.DriveContentsResult::onResult");
                        DriveContents driveContents = result.getDriveContents();

                        MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                                .setMimeType(getMimeType())
                                .setTitle(getBackupFileName()).build();
                        IntentSender intentSender = Drive.DriveApi
                                .newCreateFileActivityBuilder()
                                .setInitialMetadata(metadataChangeSet)
                                .setInitialDriveContents(driveContents)
                                .setActivityTitle(stringResources.getString(R.string.title_activity_archive))

                                .build(getGoogleApiClient());
                        try {
                            startIntentSenderForResult(
                                    intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            Timber.w("Unable to send intent %s", e.getMessage());
                        }
                    }
                });
    }


    public String getBackupFileName() {
        String name = new String("yearly-backup-");
        name += clock.timestamp() + ".json";
        Timber.d("backup file: %s", name);
        return name;
    }


    @Override public void archiveSucceeded() {
        showMessage(stringResources.getString(R.string.archive_activity_success));
    }

    @Override public void archiveFailed() {
        showMessage(stringResources.getString(R.string.archive_activity_failed));
    }
}
