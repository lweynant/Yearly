package com.lweynant.yearly.controller.archive;

import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.util.Iterator;

import timber.log.Timber;

public class HasBackupFolderAction  {

    private GoogleApiClient googleApiClient;
    private Callback callback;

    public interface Callback{
        public void hasBackupFolder(DriveFolder folder);
    }
    public HasBackupFolderAction(){
    }
    public void execute(final String backupFolderName, GoogleApiClient googleApiClient, Callback callback) {
        this.callback = callback;
        DriveFolder rootFolder = Drive.DriveApi.getRootFolder(googleApiClient);
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, backupFolderName))
                .build();
        rootFolder.queryChildren(googleApiClient, query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(@NonNull DriveApi.MetadataBufferResult metadataBufferResult) {
                Timber.d("onResult status %s", metadataBufferResult.getStatus());
                DriveFolder backupFolder = null;
                if (metadataBufferResult.getStatus().isSuccess()){
                    MetadataBuffer buffer = metadataBufferResult.getMetadataBuffer();
                    Iterator<Metadata> iterator = buffer.iterator();
                    while (iterator.hasNext()){
                        Metadata metaData = iterator.next();
                        Timber.d("metaData " + metaData );
                        if (!metaData.isTrashed() && metaData.isFolder() ){
                            Timber.d("folder with name %s", metaData.getTitle());
                            if (backupFolderName.equals(metaData.getTitle())) {
                                backupFolder = metaData.getDriveId().asDriveFolder();
                                break;
                            }
                        }
                    }
                    buffer.release();
                }
                if (backupFolder == null) {
                    Timber.d("backup folder '%s' does not exist", backupFolderName);
                }
                else {
                    Timber.d("backup folder '%s' exists", backupFolderName);
                }
                callback.hasBackupFolder(backupFolder);
            }
        });
    }
}
