package com.lweynant.yearly.model;

import android.os.Bundle;

public interface IKeyValueArchiver {

    void writeValidatorToBundle(IValidator validator, Bundle bundle);

    IValidator readValidatorFromBundle(Bundle bundle);
}
