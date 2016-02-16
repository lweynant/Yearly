package com.lweynant.yearly.model;

import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IUniqueIdGenerator;

public class EventBuilder extends BaseEventBuilder<EventBuilder, Event> {

    public EventBuilder(IValidator validator, IKeyValueArchiver archiver, IClock clock, IUniqueIdGenerator idGenerator) {
        super(validator, archiver, clock, idGenerator);
    }

    @Override public EventBuilder getThis() {
        return this;
    }
    public Event build() {
        Event event = null;
        if (isValidEvent()) {
            if (validator.validID()){
                event = new Event(validator,validator.getName(), validator.getYear(), validator.getMonth(), validator.getDay(), clock);
            }else {
                event = new Event(validator.getName(), validator.getYear(), validator.getMonth(), validator.getDay(), clock, idGenerator);
            }
        }
        return event;
    }

}
