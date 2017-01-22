package com.lweynant.yearly.platform;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.lweynant.yearly.model.IEvent;

import java.io.File;
import java.io.IOException;

import timber.log.Timber;

public class PictureRepo implements IPictureRepo {
    private Context context;
    public PictureRepo(Context context) {
        this.context = context;
    }

    @Override public void storePicture(IEvent event, File picture) {
        Timber.d("storePicture %s, %s, %s", event.toString(), picture.getParent(), picture.getName());
        File storedFile = new File(picture.getParent(), event.getStringID());
        if (storedFile.exists()) {
            storedFile.delete();
        }
        picture.renameTo(storedFile);
        Timber.d("picture renamed to: %s", picture.toString());

    }

    @Override public boolean hasPicture(IEvent event) {
        Timber.d("has picture %s from event %s", event.getStringID(), event.toString());
        return false;
    }

    @Override public File getPicture(IEvent event) {
        Timber.d("get picture %s from event %s", event.getStringID(), event.toString());
        File pictureFile = getFile(event.getStringID());
        return pictureFile;
    }

    @NonNull public File getFile(String name) {
        File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (dir != null) {
            try {
                Timber.d("storing files in dir %s", dir.getCanonicalPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new File(dir, name);
    }

    @Override public void removePicture(IEvent event) {
        Timber.d("remove picture %s from event %s", event.getStringID(), event.toString());

    }

    @Override public File getPicture() {
        Timber.d("get file for picture");
        return getFile("picture.png");
    }
}
