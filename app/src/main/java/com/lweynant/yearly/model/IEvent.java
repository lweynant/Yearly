package com.lweynant.yearly.model;

import android.os.Bundle;

import org.joda.time.LocalDate;

public interface IEvent extends Comparable<IEvent>, IEventID {


    String KEY_NAME = "name";
    String KEY_TYPE = "type";
    String KEY_DAY = "day";
    String KEY_MONTH = "month";
    String KEY_NBR_DAYS_FOR_NOTIFICATION = "nbr_days_for_notification";
    String KEY_STRING_ID = "uuid";
    String KEY_ID = "id";
    String KEY_YEAR = "year";
    String EXTRA_KEY_EVENT = "event_key_for_full_bundle";

    public String getName();

    Integer getYearOfOrigin();

    boolean hasYearOfOrigin();

    LocalDate getDate();

    int getNbrOfDaysForNotification();

    void setNbrOfDaysForNotification(int days);

    String getType();

    void archiveTo(Bundle bundle);
}
