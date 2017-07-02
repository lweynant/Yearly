package com.lweynant.yearly.controller.archive;

import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;

import timber.log.Timber;

public class CreateBackupFolderAction  {
    public interface Callback {
        public void onResult(DriveFolder driveFolder);
    }

    private HasBackupFolderAction hasFolderAction;
    private Callback callback;



    public CreateBackupFolderAction(HasBackupFolderAction hasFolderAction){
        this.hasFolderAction = hasFolderAction;
    }

    public void execute(final String backupFolderName, GoogleApiClient googleApiClient, Callback callback) {
        this.callback = callback;
        hasFolderAction.execute(backupFolderName, googleApiClient, new HasBackupFolderAction.Callback() {
            @Override public void hasBackupFolder(DriveFolder folder) {
                if (folder == null) {
                    createFolder(backupFolderName, googleApiClient);
                }
                else {
                    callback.onResult(folder);
                }
            }
        });
    }

    private void createFolder(String backupFolderName, GoogleApiClient googleApiClient) {
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(backupFolderName).build();
        DriveFolder rootFolder = Drive.DriveApi.getRootFolder(googleApiClient);
        rootFolder.createFolder(
                googleApiClient, changeSet).setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
            @Override
            public void onResult(@NonNull DriveFolder.DriveFolderResult driveFolderResult) {
                DriveFolder backupFolder = null;
                if (driveFolderResult.getStatus().isSuccess()){
                    Timber.d("successfully created folder");
                    backupFolder = driveFolderResult.getDriveFolder();
                }
                else {
                    Timber.d("failed to create folder");
                }
                callback.onResult(backupFolder);
            }
        });
    }
}
