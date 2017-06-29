package com.lweynant.yearly.controller.archive;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
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
import com.lweynant.yearly.BaseYearlyAppComponent;
import com.lweynant.yearly.EventRepoSerializerToFileDecorator;
import com.lweynant.yearly.R;
import com.lweynant.yearly.model.EventRepoSerializer;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.platform.GoogleDriveApiClientAsyncTask;
import com.lweynant.yearly.utils.JsonStreamWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import rx.Observable;
import rx.observables.BlockingObservable;
import timber.log.Timber;

public class RestoreActivity extends BaseGDriveApiClientActivity {

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
                .setMimeType(new String[] { "text/plain", "text/html" })
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
                    showMessage("Selected file's ID: " + driveId);
                }
                finish();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public class RestoreEventsAsyncTask extends GoogleDriveApiClientAsyncTask<DriveFile, Void, Boolean> {

        public RestoreEventsAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected Boolean doInBackgroundConnected(DriveFile... args) {
            Timber.d("doInBackgroundConnected");
            DriveFile file = args[0];
            DriveApi.DriveContentsResult driveContentsResult = file.open(
                    getGoogleApiClient(), DriveFile.MODE_READ_ONLY, null).await();
            if (!driveContentsResult.getStatus().isSuccess()) {
                Timber.e("could not read file");
                return false;
            }
            DriveContents driveContents = driveContentsResult.getDriveContents();
            InputStream inputStream = driveContents.getInputStream();
            readFile(inputStream);
            com.google.android.gms.common.api.Status status =
                    driveContents.commit(getGoogleApiClient(), null).await();
            boolean success = status.getStatus().isSuccess();
            if (success) Timber.d("successfully commited content to file");
            else Timber.e("failed to commit content to file");
            return success;
        }

        private void readFile(InputStream inputStream) {
            Timber.d("readFile");
        }


        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                showMessage("Error while editing contents");
                return;
            }
            showMessage("Successfully edited contents");
        }
    }
}
