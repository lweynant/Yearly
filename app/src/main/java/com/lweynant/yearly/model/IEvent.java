package com.lweynant.yearly.model;

import org.joda.time.LocalDate;

public interface IEvent extends Comparable<IEvent>{


    public String getName();

    LocalDate getDate();

    int getNbrOfDaysForNotification();

    int getID();

    void setNbrOfDaysForNotification(int days);

    String getType();
}
