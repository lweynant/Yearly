package com.lweynant.yearly;

import android.app.Application;


import com.lweynant.yearly.model.Birthday;
import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.EventRepo;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.LocalDate;

import timber.log.Timber;

public class YearlyApp extends Application implements IRString {
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        Timber.d("onCreate");
        JodaTimeAndroid.init(this);
        EventRepo repo = EventRepo.getInstance();
        LocalDate now = LocalDate.now().plusDays(1);
        repo.add(new Birthday("Test", now.getDayOfMonth(), now.getMonthOfYear(), this));
        repo.add(new Birthday("Katinka", 10, Date.MARCH, this));
        repo.add(new Birthday("Kasper", 14, Date.MAY, this));
        repo.add(new Birthday("Ann", 5, Date.MARCH, this));
        repo.add(new Birthday("Ludwig", 8, Date.FEBRUARY, this));
        repo.add(new Birthday("Jinthe", 27, Date.OCTOBER, this));
        repo.add(new Birthday("Lis", 7, Date.NOVEMBER, this));
        repo.add(new Birthday("Caroline", 6, Date.FEBRUARY, this));
        repo.add(new Birthday("Ma", 11, Date.MARCH, this));
        repo.add(new Birthday("Janne", 24, Date.NOVEMBER, this));
        repo.add(new Birthday("Julien", 3, Date.FEBRUARY, this));
        repo.add(new Birthday("Pa", 22, Date.MAY, this));
        repo.add(new Birthday("Josephine", 29, Date.MAY, this));
        repo.add(new Birthday("Joren", 30, Date.MAY, this));
        repo.add(new Birthday("Bjorn", 22, Date.JULY, this));

    }

    @Override
    public String getStringFromId(int id)
    {
        return getResources().getString(id);
    }

}
