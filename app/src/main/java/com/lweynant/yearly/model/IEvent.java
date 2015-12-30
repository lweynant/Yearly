package com.lweynant.yearly.model;

import org.joda.time.LocalDate;

public interface IEvent extends Comparable<IEvent> {


    public String getName();

    LocalDate getDate();

    int getNbrOfDaysForNotification();

    void setNbrOfDaysForNotification(int days);

    int getID();

    String getType();
}
