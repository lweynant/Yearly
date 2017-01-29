package com.lweynant.yearly.platform;

import com.lweynant.yearly.model.IEvent;

import java.io.File;

public interface IPictureRepo {
    File storePicture(IEvent event, File picture);

    File getPicture(IEvent event);

    void removePicture(IEvent event);

    File getPicture();
}
