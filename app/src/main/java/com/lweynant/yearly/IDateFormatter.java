package com.lweynant.yearly;

import com.lweynant.yearly.model.Date;

import org.joda.time.LocalDate;

public interface IDateFormatter {
    String format(@Date.Month int month, int day);

    String format(int year, @Date.Month int month, int day);

    String format(LocalDate date, int hour);
}
