package com.lweynant.yearly.platform;

import org.joda.time.LocalDate;

public interface ITimeConvertor {
    String convert(LocalDate date, int hour);
}
