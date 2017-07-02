package com.lweynant.yearly.controller.archive;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.lweynant.yearly.BaseYearlyAppComponent;
import com.lweynant.yearly.IStringResources;
import com.lweynant.yearly.R;
import com.lweynant.yearly.model.IEventRepo;
import com.lweynant.yearly.platform.IClock;

import javax.inject.Inject;

import timber.log.Timber;


public class ArchiveActivity extends BaseGDriveApiClientActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, ArchiveEventsAsyncTask.ICallback {

    private static final int REQUEST_CODE_CREATOR = 9;
    @Inject IEventRepo repo;
    @Inject IStringResources stringResources;
    @Inject CreateSaveBackupFileIntentSenderAction createBackupFileIntentSenderAction;

    @Override protected void injectDependencies(BaseYearlyAppComponent component) {
        component.inject(this);
        super.injectDependencies(component);
    }



    @Override public void onConnected(@Nullable Bundle bundle) {
        Timber.d("onConnected");


        createBackupFileIntentSenderAction.execute(getBackupFolderName(), getBackupFileName(), getMimeType(),
                getGoogleApiClient(), new CreateSaveBackupFileIntentSenderAction.Callback() {
            @Override public void onResult(IntentSender intentSender) {
                try {
                    startIntentSenderForResult(
                            intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });

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



    @Override public void archiveSucceeded() {
        showMessage(stringResources.getString(R.string.archive_activity_success));
    }

    @Override public void archiveFailed() {
        showMessage(stringResources.getString(R.string.archive_activity_failed));
    }
}
