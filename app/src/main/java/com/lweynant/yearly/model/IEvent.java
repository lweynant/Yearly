package com.lweynant.yearly.model;

import org.joda.time.LocalDate;

public interface IEvent extends Comparable<IEvent>{


    public String getTitle();

    LocalDate getDate();
}
