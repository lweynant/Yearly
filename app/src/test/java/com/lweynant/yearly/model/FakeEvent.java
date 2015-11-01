package com.lweynant.yearly.model;

public class FakeEvent implements IEvent {
    private int day;
    private
    @Date.Month
    int month;

    public FakeEvent(@Date.Month int month, int day) {
        this.day = day;
        this.month = month;
    }

    @Override
    public String toString() {
        return String.format("%d-%d", day, month);
    }

    @Override
    public String getTitle() {
        return "fake";
    }

    @Override
    public int getDay() {
        return day;
    }

    @Override
    public int getMonth() {
        return month;
    }
}