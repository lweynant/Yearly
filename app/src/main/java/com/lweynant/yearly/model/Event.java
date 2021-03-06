package com.lweynant.yearly.model;

import android.os.Bundle;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IUniqueIdGenerator;

import org.joda.time.LocalDate;

public class Event implements IEvent {
    @Expose @SerializedName(KEY_ID) public final int id;
    @Expose @SerializedName(KEY_DAY) private final int day;
    @Expose @SerializedName(KEY_MONTH) @Date.Month private final int month;
    @Expose @SerializedName(KEY_YEAR) private Integer year;

    @Expose @SerializedName(KEY_TYPE) private final String type;
    @Expose @SerializedName(KEY_STRING_ID) private final String uuid;
    @Expose @SerializedName(KEY_NAME) private String name;
    @Expose @SerializedName(KEY_NBR_DAYS_FOR_NOTIFICATION) private int nbrDaysForNotification = 1;
    private final IClock clock;

    public Event(IEventID id, String name, Integer year, @Date.Month int month, int day, IClock clock) {
        this.name = name;
        this.day = day;
        this.month = month;
        this.clock = clock;
        this.type = getClass().getCanonicalName();
        this.uuid = id.getStringID();
        this.id = id.getID();
        this.year = year;
    }
    public Event(String name, @Date.Month int month, int day, IClock clock, IUniqueIdGenerator uniqueIdGenerator) {
        this(name, null, month, day, clock, uniqueIdGenerator);
    }
    public Event(String name, Integer year, @Date.Month int month, int day, IClock clock, IUniqueIdGenerator uniqueIdGenerator) {
        this.name = name;
        this.day = day;
        this.month = month;
        this.clock = clock;
        this.type = getClass().getCanonicalName();
        this.uuid = uniqueIdGenerator.getUniqueId();
        this.id = uniqueIdGenerator.hashCode(uuid);
        this.year = year;
    }

    @Override public String toString() {
        return name + " - " + getDate().toString("dd-MM");
    }

    @Override public String getName() {
        return name;
    }

    @Override public LocalDate getDate() {
        LocalDate eventDate = new LocalDate(clock.now().getYear(), month, day);
        if (eventDate.isBefore(clock.now())) {
            eventDate = eventDate.plusYears(1);
        }
        return eventDate;
    }
    @Override public Integer getYearOfOrigin() {
        return year;
    }
     @Override public boolean hasYearOfOrigin() {
         return year != null;
     }


    @Override public int getNbrOfDaysForNotification() {
        return nbrDaysForNotification;
    }

    @Override public void setNbrOfDaysForNotification(int days) {
        nbrDaysForNotification = days;
    }

    @Override public int getID() {
        return id;
    }

    @Override public String getStringID() {
        return uuid;
    }

    @Override public String getType() {
        return type;
    }

    @Override public void archiveTo(Bundle bundle) {
        bundle.putString(IEvent.KEY_STRING_ID, getStringID());
        bundle.putInt(IEvent.KEY_ID, getID());
        bundle.putString(IEvent.KEY_NAME, getName());
        if (hasYearOfOrigin()) {
            bundle.putInt(IEvent.KEY_YEAR, getYearOfOrigin());
        }
        bundle.putInt(IEvent.KEY_MONTH, month);
        bundle.putInt(IEvent.KEY_DAY, day);
        bundle.putInt(IEvent.KEY_NBR_DAYS_FOR_NOTIFICATION, getNbrOfDaysForNotification());
        bundle.putString(IEvent.KEY_TYPE, getType());
    }

    @Override public int compareTo(IEvent another) {
        return getDate().compareTo(another.getDate());
    }
}
