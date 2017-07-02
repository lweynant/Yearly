package com.lweynant.yearly.controller.archive;

import android.content.IntentSender;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;
import com.lweynant.yearly.IStringResources;
import com.lweynant.yearly.R;

import timber.log.Timber;

public class CreateSaveBackupFileIntentSenderAction {
    public interface Callback{
        public void onResult(IntentSender intentSender);
    }

    private Callback callback;
    private DriveFolder backupFolder;
    private final CreateBackupFolderAction createBackupFolderAction;
    private IStringResources stringResources;

    public CreateSaveBackupFileIntentSenderAction(CreateBackupFolderAction createBackupFolderAction,
                                                  IStringResources stringResources) {
        this.createBackupFolderAction = createBackupFolderAction;
        this.stringResources = stringResources;
    }

    public void execute(final String backupFolderName, final String backupFilename, final String mimeType, GoogleApiClient googleApiClient, Callback callback) {
        this.callback = callback;
        createBackupFolderAction.execute(backupFolderName, googleApiClient, new CreateBackupFolderAction.Callback() {
            @Override public void onResult(DriveFolder driveFolder) {
                backupFolder = driveFolder;
                createSaveBackupFileIntentSender(backupFilename, mimeType, googleApiClient);
            }
        });
    }


    private void createSaveBackupFileIntentSender(final String backupFilename, final String mimeType, GoogleApiClient googleApiClient) {
        this.backupFolder = backupFolder;
        Drive.DriveApi.newDriveContents(googleApiClient)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                    @Override
                    public void onResult(DriveApi.DriveContentsResult result) {
                        Timber.d("DriveApi.DriveContentsResult::onResult");
                        DriveContents driveContents = result.getDriveContents();

                        MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                                .setMimeType(mimeType)
                                .setTitle(backupFilename).build();
                        IntentSender intentSender = Drive.DriveApi
                                .newCreateFileActivityBuilder()
                                .setInitialMetadata(metadataChangeSet)
                                .setInitialDriveContents(driveContents)
                                .setActivityStartFolder(backupFolder.getDriveId())
                                .setActivityTitle(stringResources.getString(R.string.title_activity_archive))

                                .build(googleApiClient);
                        callback.onResult(intentSender);

                    }
                });

    }
}
