package com.lweynant.yearly;

import android.app.Application;


import com.lweynant.yearly.model.Birthday;
import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.util.Clock;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.LocalDate;

import timber.log.Timber;

public class YearlyApp extends Application implements IRString {
    private EventRepo repo;

    public EventRepo getRepo()  {
        if (null == repo){
            Timber.e("repo was not initialized");
            Clock clock = new Clock();
            repo = new EventRepo(clock);
            LocalDate now = LocalDate.now().plusDays(1);
            //repo.add(new Birthday("Test", now.getDayOfMonth(), now.getMonthOfYear(), this));
            repo.add(new Birthday("Katinka", Date.MARCH, 10, clock, this));
            repo.add(new Birthday("Kasper", Date.MAY, 14, clock, this));
            repo.add(new Birthday("Ann", Date.MARCH, 5, clock, this));
            repo.add(new Birthday("Ludwig", Date.FEBRUARY, 8, clock, this));
            repo.add(new Birthday("Jinthe", Date.OCTOBER, 27, clock, this));
            repo.add(new Birthday("Lis", Date.NOVEMBER, 7, clock,this));
            repo.add(new Birthday("Caroline", Date.FEBRUARY, 6, clock, this));
            repo.add(new Birthday("Ma", Date.MARCH, 11, clock,this));
            repo.add(new Birthday("Janne", Date.NOVEMBER, 24, clock, this));
            repo.add(new Birthday("Julien", Date.FEBRUARY, 3, clock, this));
            repo.add(new Birthday("Pa", Date.MAY, 22, clock,this));
            repo.add(new Birthday("Josephine", Date.MAY, 29, clock,this));
            repo.add(new Birthday("Joren", Date.MAY, 30, clock, this));
            repo.add(new Birthday("Bjorn", Date.JULY, 22, clock,this));
        }
        Timber.d("getRepo");
        return repo;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        Timber.d("onCreate");
        JodaTimeAndroid.init(this);
        getRepo();
    }


    @Override
    public String getStringFromId(int id)
    {
        return getResources().getString(id);
    }

}
