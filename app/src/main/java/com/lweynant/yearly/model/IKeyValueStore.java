package com.lweynant.yearly.model;

import android.os.Bundle;

public interface IKeyValueStore {
    String KEY_NAME = "name";
    String KEY_YEAR = "year";
    String KEY_MONTH = "month";
    String KEY_DAY = "day";

    void writeValidatorToBundle(IValidator validator, Bundle bundle);

    IValidator readValidatorFromBundle(Bundle bundle);
}
