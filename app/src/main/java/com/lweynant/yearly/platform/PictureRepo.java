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

    @Override public File storePicture(IEvent event, File picture) {
        Timber.d("storePicture %s, %s, %s", event.toString(), picture.getParent(), picture.getName());
        File storedFile = new File(picture.getParent(), event.getStringID());
        Timber.d("will store file in %s", storedFile.toString());
        if (storedFile.exists()) {
            Timber.d("file exists, will delete it");
            storedFile.delete();
        }
        if (picture.renameTo(storedFile)) {
            Timber.d("picture renamed to: %s", storedFile.toString());
            return storedFile;
        }
        else {
            Timber.d("renaming failed, %s", picture.toString());
            return picture;
        }

    }


    @Override public File getPicture(IEvent event) {
        Timber.d("get picture %s from event %s", event.getStringID(), event.toString());
        File pictureFile = getFile(event.getStringID());
        return pictureFile;
    }

    @NonNull public File getFile(String name) {
        Timber.d("get picture %s", name);
        File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, name);
    }

    @Override public void removePicture(IEvent event) {
        Timber.d("remove picture %s from event %s", event.getStringID(), event.toString());
        File picture = getPicture(event);
        picture.delete();
    }

    @Override public File getPicture() {
        Timber.d("get file for picture");
        return getFile("picture.png");
    }
}
