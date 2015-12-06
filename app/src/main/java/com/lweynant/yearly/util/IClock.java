package com.lweynant.yearly.util;

import org.joda.time.LocalDate;

public interface IClock {
    LocalDate now();

    String timestamp();
}
