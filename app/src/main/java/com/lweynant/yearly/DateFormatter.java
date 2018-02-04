package com.lweynant.yearly;

import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.platform.IClock;

import org.joda.time.LocalDate;

public class DateFormatter implements IDateFormatter {
    private final IStringResources rstring;

    public DateFormatter(IStringResources rstring) {
        this.rstring = rstring;
    }


    @Override public String format(@Date.Month int month, int day) {
        String[] months = rstring.getStringArray(R.array.months_day);
        return String.format(months[month], day);
    }

    @Override public String format(int year, @Date.Month int month, int day) {
        String[] months = rstring.getStringArray(R.array.year_months_day);
        return String.format(months[month],year, day);
    }

    @Override public String format(LocalDate date, int hour) {
        String[] months = rstring.getStringArray(R.array.year_months_day_hour);
        return String.format(months[date.getMonthOfYear()],date.getYear(), date.getDayOfMonth(), hour);
    }

    @Override public String format(IClock clock) {
        LocalDate now = clock.now();
        String time = format(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth());
        time = String.format("%02d:%02d:%02d, %s", clock.hour(), clock.minutes(), clock.seconds(), time);
        return time;
    }

}
