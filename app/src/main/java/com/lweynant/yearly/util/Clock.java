package com.lweynant.yearly.util;

import org.joda.time.LocalDate;

public class Clock implements IClock {
    @Override
    public LocalDate now() {
        return LocalDate.now();
    }
}
