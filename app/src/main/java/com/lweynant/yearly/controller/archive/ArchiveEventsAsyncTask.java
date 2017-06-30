package com.lweynant.yearly.controller.archive;

import android.content.Context;

import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.lweynant.yearly.EventRepoSerializerToFileDecorator;
import com.lweynant.yearly.model.EventRepoSerializer;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.IEventRepo;
import com.lweynant.yearly.platform.GoogleDriveApiClientAsyncTask;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.utils.JsonStreamWriter;

import java.io.IOException;
import java.io.OutputStream;

import rx.Observable;
import rx.observables.BlockingObservable;
import timber.log.Timber;

public class ArchiveEventsAsyncTask extends GoogleDriveApiClientAsyncTask<DriveFile, Void, Boolean> {

    private IEventRepo repo;
    private ICallback callback;
    private IClock clock;

    public interface ICallback {
        public void archiveSucceeded();

        public void archiveFailed();
    }

    public ArchiveEventsAsyncTask(Context context, IEventRepo repo, ICallback callback, IClock clock) {
        super(context);
        this.repo = repo;
        this.callback = callback;
        this.clock = clock;
    }

    @Override   protected Boolean doInBackgroundConnected(DriveFile... args) {
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

    @Override protected void onPostExecute(Boolean result) {
        if (!result) {
            callback.archiveFailed();
            return;
        }
        callback.archiveSucceeded();
    }
}