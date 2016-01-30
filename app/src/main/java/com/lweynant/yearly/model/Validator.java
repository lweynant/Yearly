package com.lweynant.yearly.model;

import android.os.Bundle;

public class Validator implements IValidator {

    private String name;
    private int dayOfMonth;
    private @Date.Month int month;
    private Integer year;

    public Validator() {
    }

    @Override public boolean validYear() {
        return year != null;
    }

    @Override public boolean validDay() {
        return dayOfMonth > 0 && dayOfMonth <= 31;
    }

    @Override public boolean validMonth() {
        return month > 0 && month <= 12;
    }

    @Override public boolean validName() {
        return validString(name);
    }

    @Override public void setName(String name) {
        this.name = name;
    }

    @Override public String getName() {
        return name;
    }

    @Override public void setDay(int day) {
        this.dayOfMonth = day;
    }

    @Override public int getDay() {
        return dayOfMonth;
    }

    @Override public void setMonth(@Date.Month int month) {
        this.month = month;
    }

    @Override public @Date.Month int getMonth() {
        return month;
    }

    @Override public void setYear(int year) {
        this.year = year;
    }

    @Override public Integer getYear() {
        return year;
    }

    @Override public void clearYear() {
        year = null;
    }

    @Override public boolean validString(String string) {
        return string != null && !string.isEmpty();
    }

}