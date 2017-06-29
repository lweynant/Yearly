package com.lweynant.yearly.controller.archive;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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
import com.lweynant.yearly.controller.BaseActivity;
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

public class ArchiveActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 7;
    private static final int REQUEST_CODE_CREATOR = 9;
    private GoogleApiClient mGoogleApiClient;
    @Inject IClock clock;
    @Inject IEventRepo repo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .build();
    }

    @Override protected void injectDependencies(BaseYearlyAppComponent component) {
        component.inject(this);
    }


    @Override public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Timber.d("onConnectionFailed");
        if (connectionResult.hasResolution()) {
            //todo check, I think that for enableAutoManage api client this is handled
            try {
                Timber.i("calling connectionResult.startResolutionForResult()");
                connectionResult.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                Timber.i("error thrown..");
                Timber.e(e.getMessage());
            }
        } else {
            Timber.d("connectionResult has no resolution");
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
        }
    }
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        Timber.d("onActivityResult requestCode %d resultCode %d", requestCode, resultCode);
        switch (requestCode) {
            case RESOLVE_CONNECTION_REQUEST_CODE:
                Timber.i("RESOLVE_CONNECTION_REQUEST_CODE");
                if (resultCode == RESULT_OK) {
                    Timber.i("calling connect on googleApiClient");
                    mGoogleApiClient.connect();
                }
                break;
            case REQUEST_CODE_CREATOR:
                Timber.i("REQUEST_CODE_CREATOR");
                if (resultCode == RESULT_OK) {
                    DriveId driveId = (DriveId) data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                    Timber.i("File created with ID: " + driveId);
                    DriveFile file = driveId.asDriveFile();
                    new ArchiveEventsAsyncTask(getApplicationContext()).execute(file);
                }
                finish();
                break;
        }
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override public void onConnected(@Nullable Bundle bundle) {
        Timber.d("onConnected");
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(driveContentsCallback);
    }

    @Override public void onConnectionSuspended(int i) {
        Timber.d("onConnectionSuspended");
    }

    final ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    Timber.d("DriveApi.DriveContentsResult::onResult");
                    DriveContents driveContents = result.getDriveContents();

                    MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                            .setMimeType("text/html")
                            .setTitle(getBackupFileName()).build();
                    IntentSender intentSender = Drive.DriveApi
                            .newCreateFileActivityBuilder()
                            .setInitialMetadata(metadataChangeSet)
                            .setInitialDriveContents(driveContents)
                            .setActivityTitle("save backup file")

                            .build(mGoogleApiClient);
                    try {
                        startIntentSenderForResult(
                                intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                        Timber.w("Unable to send intent %s", e.getMessage());
                    }
                }
            };

    public String getBackupFileName() {
        String name = new String("yearly-backup-");
        name += clock.timestamp() + ".json";
        Timber.d("backup file: %s", name);
        return name;
    }

    public class ArchiveEventsAsyncTask extends GoogleDriveApiClientAsyncTask<DriveFile, Void, Boolean> {

        public ArchiveEventsAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected Boolean doInBackgroundConnected(DriveFile... args) {
            Timber.d("doInBackgroundConnected");
            DriveFile file = args[0];
            try {
                DriveApi.DriveContentsResult driveContentsResult = file.open(
                        getGoogleApiClient(), DriveFile.MODE_WRITE_ONLY, null).await();
                if (!driveContentsResult.getStatus().isSuccess()) {
                    Timber.e("could not write file");
                    return false;
                }
                DriveContents driveContents = driveContentsResult.getDriveContents();
                OutputStream outputStream = driveContents.getOutputStream();
                writeFile(outputStream);
                com.google.android.gms.common.api.Status status =
                        driveContents.commit(getGoogleApiClient(), null).await();
                boolean success = status.getStatus().isSuccess();
                if (success) Timber.d("successfully commited content to file");
                else Timber.e("failed to commit content to file");
                return success;
            } catch (IOException e) {
                Timber.e("IOException while appending to the output stream: %s", e.getMessage());
            }
            return false;
        }

        private void writeFile(OutputStream outputStream) throws IOException {
            Observable<IEvent> events = repo.getEvents();
            Timber.i("archive");
            JsonStreamWriter streamWriter = new JsonStreamWriter(outputStream);
            BlockingObservable blockingEvents = BlockingObservable.from(events);
            blockingEvents.subscribe(new EventRepoSerializerToFileDecorator(streamWriter, new EventRepoSerializer(clock)));

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
