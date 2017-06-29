package com.lweynant.yearly.controller.archive;

import android.content.Context;

import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.lweynant.yearly.model.IEventRepo;
import com.lweynant.yearly.platform.GoogleDriveApiClientAsyncTask;

import java.io.InputStream;

import timber.log.Timber;

public class RestoreEventsAsyncTask extends GoogleDriveApiClientAsyncTask<DriveFile, Void, Boolean> {


    private final IEventRepo repo;
    private final ICallback callback;

    public interface ICallback {
        public void restoreSucceeded();
        public void restoreFailed();
    }
    public RestoreEventsAsyncTask(Context context, IEventRepo repo, ICallback callback) {
        super(context);
        this.repo = repo;
        this.callback = callback;
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

        return true;
    }

    private void readFile(InputStream inputStream) {
        Timber.d("readFile");
        repo.restore(inputStream);
    }


    @Override
    protected void onPostExecute(Boolean result) {
        if (!result) {
            callback.restoreFailed();
            return;
        }
        callback.restoreSucceeded();

    }
}

