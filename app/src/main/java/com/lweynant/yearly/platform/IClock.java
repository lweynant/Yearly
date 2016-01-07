package com.lweynant.yearly.platform;

import org.joda.time.LocalDate;

public interface IClock {
    LocalDate now();

    String timestamp();
}
