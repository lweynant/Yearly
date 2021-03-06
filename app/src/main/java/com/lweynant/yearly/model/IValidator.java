package com.lweynant.yearly.model;

public interface IValidator extends IEventID {
    boolean validYear();

    boolean validDay();

    boolean validMonth();

    boolean validName();

    void setName(String name);

    String getName();

    void setDay(int day);

    int getDay();

    void setMonth(@Date.Month int month);

    @Date.Month int getMonth();

    void setYear(int year);

    Integer getYear();

    void clearYear();

    boolean validString(String string);

    boolean validID();

    void setID(String stringId, int id);

}
