package com.lweynant.yearly.model;

import android.os.Bundle;

public class KeyValueArchiver implements IKeyValueArchiver {
    private final ValidatorFactory validatorFactory;

    KeyValueArchiver(ValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
    }
    @Override public void writeValidatorToBundle(IValidator validator, Bundle bundle) {
        if (validator.validID()) {
            bundle.putString(KEY_STRING_ID, validator.getStringID());
            bundle.putInt(KEY_ID, validator.getID());
        } else {
            bundle.remove(KEY_ID);
            bundle.remove(KEY_STRING_ID);
        }

        if (validator.validName()) {
            bundle.putString(KEY_NAME, validator.getName());
        } else {
            bundle.remove(KEY_NAME);
        }
        if (validator.validYear()) {
            bundle.putInt(KEY_YEAR, validator.getYear());
        } else {
            bundle.remove(KEY_YEAR);
        }
        if (validator.validMonth()) {
            bundle.putInt(KEY_MONTH, validator.getMonth());
        } else {
            bundle.remove(KEY_MONTH);
        }
        if (validator.validDay()) {
            bundle.putInt(KEY_DAY, validator.getDay());
        } else {
            bundle.remove(KEY_DAY);
        }
    }

    @Override public IValidator readValidatorFromBundle(Bundle bundle) {
        IValidator validator = validatorFactory.create();
        if (bundle.containsKey(KEY_NAME)) {
            validator.setName(bundle.getString(KEY_NAME));
        }
        if (bundle.containsKey(KEY_YEAR)) {
            validator.setYear(bundle.getInt(KEY_YEAR));
        }
        if (bundle.containsKey(KEY_MONTH)) {
            //noinspection ResourceType
            validator.setMonth(bundle.getInt(KEY_MONTH));
        }
        if (bundle.containsKey(KEY_DAY)) {
            validator.setDay(bundle.getInt(KEY_DAY));
        }
        if (bundle.containsKey(KEY_ID) && bundle.containsKey(KEY_STRING_ID)){
            validator.setID(bundle.getString(KEY_STRING_ID), bundle.getInt(KEY_ID));
        }
        return validator;
    }


}
