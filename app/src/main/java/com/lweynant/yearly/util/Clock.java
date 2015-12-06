package com.lweynant.yearly.util;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class Clock implements IClock {
    @Override
    public LocalDate now() {
        return LocalDate.now();
    }

    @Override
    public String timestamp() {
        return DateTime.now().toString();
    }
}
