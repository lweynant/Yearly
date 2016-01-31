package com.lweynant.yearly.model;

import android.os.Bundle;

import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IUniqueIdGenerator;

public class EventBuilder {

    private IValidator validator;
    private IKeyValueArchiver archiver;
    private IClock clock;
    private IUniqueIdGenerator idGenerator;

    public EventBuilder(IValidator validator, IKeyValueArchiver archiver, IClock clock, IUniqueIdGenerator idGenerator) {
        this.validator = validator;
        this.archiver = archiver;
        this.clock = clock;
        this.idGenerator = idGenerator;
    }
    public IEvent build() {
        Event event = null;
        if (validator.validName() && validator.validMonth() && validator.validDay()) {
            event = new Event(validator.getName(), validator.getYear(), validator.getMonth(), validator.getDay(), clock, idGenerator);
        }
        return event;
    }

    public EventBuilder setName(String name) {
        validator.setName(name);
        return this;
    }

    public EventBuilder setYear(int year) {
        validator.setYear(year);
        return this;
    }

    public EventBuilder clearYear() {
        validator.clearYear();
        return this;
    }

    public EventBuilder setMonth(@Date.Month int month) {
        validator.setMonth(month);
        return this;
    }

    public EventBuilder setDay(int day) {
        validator.setDay(day);
        return this;
    }

    public void archiveTo(Bundle bundle) {
        archiver.writeValidatorToBundle(validator, bundle);
    }

    public void set(Bundle bundle) {
        validator = archiver.readValidatorFromBundle(bundle);
    }
}
