package com.lweynant.yearly.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lweynant.yearly.IRString;
import com.lweynant.yearly.R;
import com.lweynant.yearly.YearlyApp;
import com.lweynant.yearly.util.Clock;
import com.lweynant.yearly.util.IClock;
import com.lweynant.yearly.util.IUUID;

import org.joda.time.LocalDate;


public class Birthday extends Event {

    public static final String KEY_NAME="name";
    public static final String KEY_YEAR_OF_BIRTH = "year";

    private final IRString rstring;
    @Expose
    @SerializedName(KEY_NAME)
    private String name;

    @Expose
    @SerializedName(KEY_YEAR_OF_BIRTH)
    private Integer yearOfBirth;

    public Birthday(String name, @Date.Month int month, int day, IClock clock, IUUID iuuid, IRString rstring) {
        super(month, day, clock, iuuid);
        this.rstring = rstring;
        this.name = name;
        super.setNbrOfDaysForNotification(2);
    }

    public Birthday(String name, int yearOfBirth, @Date.Month int month, int day, IClock clock, IUUID iuuid, IRString rstring) {
        this(name, month, day, clock, iuuid, rstring);
        this.yearOfBirth = yearOfBirth;
    }


    @Override
    public String getTitle() {
        return String.format(rstring.getStringFromId(R.string.birthday_from), name);
    }

}
