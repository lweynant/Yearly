package com.lweynant.yearly.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lweynant.yearly.util.IClock;
import com.lweynant.yearly.util.IUniqueIdGenerator;

import org.joda.time.Days;
import org.joda.time.LocalDate;

public class Event implements IEvent {
    public static final String KEY_TYPE = "type";
    public static final String KEY_DAY = "day";
    public static final String KEY_MONTH = "month";
    public static final String KEY_NBR_DAYS_FOR_NOTIFICATION = "nbr_days_for_notification";
    public static final String KEY_UID = "uuid";
    public static final String KEY_ID = "id";

    @Expose
    @SerializedName(KEY_DAY)
    private final int day;
    @Date.Month
    @Expose
    @SerializedName(KEY_MONTH)
    private final int month;
    @Expose
    @SerializedName(KEY_TYPE)
    private final String type;
    @Expose
    @SerializedName(KEY_UID)
    private final String uuid;
    @Expose
    @SerializedName(KEY_ID)
    public final int id;
    @Expose
    @SerializedName(KEY_NBR_DAYS_FOR_NOTIFICATION)
    private int nbrDaysForNotification = 1;

    private final IClock clock;

    public Event( @Date.Month int month, int day, IClock clock, IUniqueIdGenerator uniqueIdGenerator) {
        this.day = day;
        this.month = month;
        this.clock = clock;
        this.type = getClass().getCanonicalName();
        this.uuid = uniqueIdGenerator.getRandomUID();
        this.id = uniqueIdGenerator.hashCode(uuid);
    }

    @Override
    public String toString() {
        return getDate().toString();
    }

    @Override
    public int hashCode() {
        return getDate().hashCode();
    }

    @Override
    public String getTitle() {
        return "";
    }


    @Override
    public LocalDate getDate() {
        LocalDate eventDate = new LocalDate(clock.now().getYear(), month, day);
        if (eventDate.isBefore(clock.now())) {
            eventDate = eventDate.plusYears(1);
        }
        return eventDate;
    }

    @Override
    public int getNbrOfDaysForNotification() {
        return nbrDaysForNotification;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void setNbrOfDaysForNotification(int days) {
        nbrDaysForNotification = days;
    }

    @Override
    public String getType() {
        return type;
    }


    @Override
    public int compareTo(IEvent another) {
        return getDate().compareTo(another.getDate());
    }

    public static TimeBeforeNotification timeBeforeNotification(LocalDate from, IEvent event) {
        LocalDate eventDate = event.getDate();
        if (eventDate.isBefore(from)) {
            eventDate = eventDate.plusYears(1);
        }
        int days = Days.daysBetween(from, eventDate).getDays();
        if (days > 0) {
            days = days - event.getNbrOfDaysForNotification();
            days = days < 0 ? 0 : days;
        }
        int morning = 6;
        int evening = 19;
        int hour = from.isEqual(eventDate)? morning : evening;

        return new TimeBeforeNotification(days, hour);
    }

    public static boolean shouldBeNotified(LocalDate from, IEvent event) {
        int days = Days.daysBetween(from, event.getDate()).getDays();
        return days >= 0 && days <= event.getNbrOfDaysForNotification();
    }
}
