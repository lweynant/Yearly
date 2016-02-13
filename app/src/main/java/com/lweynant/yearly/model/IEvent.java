package com.lweynant.yearly.model;

import org.joda.time.LocalDate;

public interface IEvent extends Comparable<IEvent>, IEventID {


    public String getName();

    Integer getYearOfOrigin();

    boolean hasYearOfOrigin();

    LocalDate getDate();

    int getNbrOfDaysForNotification();

    void setNbrOfDaysForNotification(int days);

    String getType();

}
