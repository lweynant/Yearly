package com.lweynant.yearly.model;

import android.os.Bundle;

public class KeyValueArchiver implements IKeyValueArchiver {
    private final ValidatorFactory validatorFactory;

    KeyValueArchiver(ValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
    }
    @Override public void writeValidatorToBundle(IValidator validator, Bundle bundle) {
        if (validator.validID()) {
            bundle.putString(IEvent.KEY_STRING_ID, validator.getStringID());
            bundle.putInt(IEvent.KEY_ID, validator.getID());
        } else {
            bundle.remove(IEvent.KEY_ID);
            bundle.remove(IEvent.KEY_STRING_ID);
        }

        if (validator.validName()) {
            bundle.putString(IEvent.KEY_NAME, validator.getName());
        } else {
            bundle.remove(IEvent.KEY_NAME);
        }
        if (validator.validYear()) {
            bundle.putInt(IEvent.KEY_YEAR, validator.getYear());
        } else {
            bundle.remove(IEvent.KEY_YEAR);
        }
        if (validator.validMonth()) {
            bundle.putInt(IEvent.KEY_MONTH, validator.getMonth());
        } else {
            bundle.remove(IEvent.KEY_MONTH);
        }
        if (validator.validDay()) {
            bundle.putInt(IEvent.KEY_DAY, validator.getDay());
        } else {
            bundle.remove(IEvent.KEY_DAY);
        }
    }

    @Override public IValidator readValidatorFromBundle(Bundle bundle) {
        IValidator validator = validatorFactory.create();
        if (bundle.containsKey(IEvent.KEY_NAME)) {
            validator.setName(bundle.getString(IEvent.KEY_NAME));
        }
        if (bundle.containsKey(IEvent.KEY_YEAR)) {
            validator.setYear(bundle.getInt(IEvent.KEY_YEAR));
        }
        if (bundle.containsKey(IEvent.KEY_MONTH)) {
            //noinspection ResourceType
            validator.setMonth(bundle.getInt(IEvent.KEY_MONTH));
        }
        if (bundle.containsKey(IEvent.KEY_DAY)) {
            validator.setDay(bundle.getInt(IEvent.KEY_DAY));
        }
        if (bundle.containsKey(IEvent.KEY_ID) && bundle.containsKey(IEvent.KEY_STRING_ID)){
            validator.setID(bundle.getString(IEvent.KEY_STRING_ID), bundle.getInt(IEvent.KEY_ID));
        }
        return validator;
    }


}
