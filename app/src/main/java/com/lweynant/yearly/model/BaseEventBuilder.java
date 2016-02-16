package com.lweynant.yearly.model;

import android.os.Bundle;

import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IUniqueIdGenerator;

public abstract class BaseEventBuilder<T extends BaseEventBuilder, E extends IEvent>{
    protected IValidator validator;
    private final IKeyValueArchiver archiver;
    protected final IClock clock;
    protected final IUniqueIdGenerator idGenerator;

    public BaseEventBuilder(IValidator validator, IKeyValueArchiver archiver, IClock clock, IUniqueIdGenerator idGenerator) {
        this.archiver = archiver;
        this.idGenerator = idGenerator;
        this.validator = validator;
        this.clock = clock;
    }
    public abstract T getThis();

    public abstract E build();

    protected boolean isValidEvent() {
        return validator.validName() && validator.validMonth() && validator.validDay();
    }

    public T setName(String name) {
        validator.setName(name);
        return getThis();
    }

    public T setYear(int year) {
        validator.setYear(year);
        return getThis();
    }

    public T clearYear() {
        validator.clearYear();
        return getThis();
    }

    public T setMonth(@Date.Month int month) {
        validator.setMonth(month);
        return getThis();
    }

    public T setDay(int day) {
        validator.setDay(day);
        return getThis();
    }

    public void archiveTo(Bundle bundle) {
        archiver.writeValidatorToBundle(validator, bundle);
    }

    public T set(Bundle bundle) {
        validator = archiver.readValidatorFromBundle(bundle);
        return getThis();
    }

    public T setID(String stringID, int ID) {
        validator.setID(stringID, ID);
        return getThis();
    }
}
