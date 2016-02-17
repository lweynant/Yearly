package com.lweynant.yearly.model;

import android.os.Bundle;

import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IUniqueIdGenerator;



public class BirthdayBuilder extends BaseEventBuilder<BirthdayBuilder, Birthday> {
    public static final String KEY_LAST_NAME = "last_name";
    private String lastName;

    public BirthdayBuilder(IValidator validator, IKeyValueArchiver archiver,
                           IClock clock, IUniqueIdGenerator uniqueIdGenerator) {
        super(validator, archiver, clock, uniqueIdGenerator);
    }

    @Override public BirthdayBuilder getThis() {
        return this;
    }
    @Override public boolean canBuild() {
        return isValidEvent();
    }

    @Override public Birthday build() {
        if (canBuild()) {
            if (validator.validID()) {
                return new Birthday(validator, validator.getName(),
                        lastName,
                        validator.getYear(),
                        validator.getMonth(),
                        validator.getDay(),
                        clock);
            }
            return new Birthday(validator.getName(),
                        lastName,
                        validator.getYear(),
                        validator.getMonth(),
                        validator.getDay(),
                        clock, idGenerator);
        }
        return null;
    }


    @Override public void archiveTo(Bundle bundle) {
        super.archiveTo(bundle);
        if (validator.validString(lastName)) {
            bundle.putString(KEY_LAST_NAME, lastName);
        } else {
            bundle.remove(KEY_LAST_NAME);
        }
    }
    @Override public BirthdayBuilder set(Bundle bundle) {
        super.set(bundle);
        if (bundle.containsKey(KEY_LAST_NAME)) {
            lastName = bundle.getString(KEY_LAST_NAME);
        }
        return this;
    }


    public BirthdayBuilder setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

}
