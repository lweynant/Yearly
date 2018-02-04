package com.lweynant.yearly.platform;

import org.joda.time.LocalDate;

public interface IClock {
    LocalDate now();

    int hour();

    int minutes();

    String timestamp();

    int seconds();
}
