package com.lweynant.yearly.controller.archive;

import android.content.IntentSender;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.lweynant.yearly.IStringResources;
import com.lweynant.yearly.R;

public class CreateRestoreBackupFileIntentSenderAction {
    public interface Callback{
        public void onResult(IntentSender intentSender);
    }

    private HasBackupFolderAction hasBackupFolderAction;
    private IStringResources stringResources;

    public CreateRestoreBackupFileIntentSenderAction(HasBackupFolderAction hasBackupFolderAction, IStringResources stringResources){
        this.hasBackupFolderAction = hasBackupFolderAction;
        this.stringResources = stringResources;
    }

    public void execute(final String backupFolderName, final String[] mimeTypes, GoogleApiClient googleApiClient, Callback callback){
        hasBackupFolderAction.execute(backupFolderName, googleApiClient, new HasBackupFolderAction.Callback() {
            @Override public void hasBackupFolder(DriveFolder folder) {
                OpenFileActivityBuilder openFileActivityBuilder = Drive.DriveApi
                        .newOpenFileActivityBuilder()
                        .setMimeType(mimeTypes)
                        .setActivityTitle(stringResources.getString(R.string.title_activity_restore));
                if (folder != null){
                    openFileActivityBuilder.setActivityStartFolder(folder.getDriveId());
                }
                IntentSender intentSender = openFileActivityBuilder
                        .build(googleApiClient);
                callback.onResult(intentSender);
            }
        });

    }
}
