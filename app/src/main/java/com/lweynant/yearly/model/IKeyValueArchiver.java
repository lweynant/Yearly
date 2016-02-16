package com.lweynant.yearly.model;

import android.os.Bundle;

public interface IKeyValueArchiver {
    String KEY_NAME = "name";
    String KEY_YEAR = "year";
    String KEY_MONTH = "month";
    String KEY_DAY = "day";
    String KEY_STRING_ID = "string_id";
    String KEY_ID = "id";

    void writeValidatorToBundle(IValidator validator, Bundle bundle);

    IValidator readValidatorFromBundle(Bundle bundle);
}
