package com.lweynant.yearly.model;

import android.os.Bundle;

import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IUniqueIdGenerator;



public class BirthdayBuilder {
    public static final String KEY_NAME = IKeyValueArchiver.KEY_NAME;
    public static final String KEY_YEAR = IKeyValueArchiver.KEY_YEAR;
    public static final String KEY_MONTH = IKeyValueArchiver.KEY_MONTH;
    public static final String KEY_DAY = IKeyValueArchiver.KEY_DAY;

    public static final String KEY_LAST_NAME = "last_name";
    private final IClock clock;
    private final IUniqueIdGenerator uniquedIdGenerator;
    private IValidator validator;
    private String lastName;
    private IKeyValueArchiver storage;

    public BirthdayBuilder(IValidator validator, IKeyValueArchiver storage,
                           IClock clock, IUniqueIdGenerator uniqueIdGenerator) {
        this.validator = validator;
        this.storage = storage;
        this.clock = clock;
        this.uniquedIdGenerator = uniqueIdGenerator;
    }

    public Birthday build() {
        if (validator.validName() && validator.validMonth() && validator.validDay()) {
            if (validator.validYear()) {
                return new Birthday(validator.getName(),
                        lastName,
                        validator.getYear(),
                        validator.getMonth(),
                        validator.getDay(),
                        clock, uniquedIdGenerator);
            } else {
                return new Birthday(validator.getName(),
                        lastName,
                        validator.getMonth(),
                        validator.getDay(),
                        clock, uniquedIdGenerator);
            }
        }
        return null;
    }

    public BirthdayBuilder setName(String newName) {
        validator.setName(newName);
        return this;
    }

    public BirthdayBuilder setDay(int day) {
        validator.setDay(day);
        return this;
    }

    public BirthdayBuilder setMonth(@Date.Month int month) {
        validator.setMonth(month);
        return this;
    }

    public BirthdayBuilder setYear(int year) {
        validator.setYear(year);
        return this;
    }

    public void archiveTo(Bundle bundle) {
        storage.writeValidatorToBundle(validator, bundle);
        if (validator.validString(lastName)) {
            bundle.putString(KEY_LAST_NAME, lastName);
        } else {
            bundle.remove(KEY_LAST_NAME);
        }


    }

    public BirthdayBuilder setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public BirthdayBuilder set(Bundle bundle) {
        validator = storage.readValidatorFromBundle(bundle);
        if (bundle.containsKey(KEY_LAST_NAME)) {
            lastName = bundle.getString(KEY_LAST_NAME);
        }
        return this;
    }

    public BirthdayBuilder clearYear() {
        validator.clearYear();
        return this;
    }
}
