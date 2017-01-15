package com.lweynant.yearly.platform;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class TimeConvertor implements ITimeConvertor {
    @Override public String convert(LocalDate date, int hour) {
        DateTime time = new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), hour, 0);
        return time.toString();
    }
}
