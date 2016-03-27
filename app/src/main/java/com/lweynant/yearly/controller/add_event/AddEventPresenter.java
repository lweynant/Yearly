package com.lweynant.yearly.controller.add_event;

import android.os.Bundle;

import com.lweynant.yearly.controller.DateFormatter;
import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.EventBuilder;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.ITransaction;
import com.lweynant.yearly.platform.IClock;

import org.joda.time.LocalDate;

import rx.Observable;
import rx.subscriptions.CompositeSubscription;

public class AddEventPresenter implements AddEventContract.UserActionListener{

    private AddEventContract.FragmentView fragmentView;
    private EventBuilder builder;
    private ITransaction transaction;
    private DateFormatter dateFormatter;
    private IClock clock;
    private CompositeSubscription subcription = new CompositeSubscription();
    private boolean update = false;

    public AddEventPresenter(EventBuilder builder, ITransaction transaction, DateFormatter dateFormatter, IClock clock){
        this.builder = builder;
        this.transaction = transaction;
        this.dateFormatter = dateFormatter;
        this.clock = clock;
    }

    @SuppressWarnings("ResourceType") @Override
    public void initialize(AddEventContract.FragmentView fragmentView, Bundle args) {
        this.fragmentView = fragmentView;
        LocalDate now = clock.now();
        builder.set(args);
        int selectedYear = readIntFromBundle(args, IEvent.KEY_YEAR, now.getYear());
        int selectedMonth = readIntFromBundle(args, IEvent.KEY_MONTH, now.getMonthOfYear());
        int selectedDay = readIntFromBundle(args, IEvent.KEY_DAY, now.getDayOfMonth());
        String name = readStringFromBundle(args, IEvent.KEY_NAME, null);
        String formattedDate = null;
        if (args.containsKey(IEvent.KEY_DAY) && args.containsKey(IEvent.KEY_MONTH)) {
            if (args.containsKey(IEvent.KEY_YEAR)) {
                formattedDate = dateFormatter.format(selectedYear, selectedMonth, selectedDay);
            } else {
                formattedDate = dateFormatter.format(selectedMonth, selectedDay);
            }
        }
        fragmentView.initialize(name, formattedDate, selectedYear, selectedMonth, selectedDay);
    }
    private String readStringFromBundle(Bundle bundle, String key, String defaultValue) {
        if (bundle.containsKey(key)) {
            return bundle.getString(key);
        }
        return defaultValue;
    }

    private int readIntFromBundle(Bundle bundle, String key, int defaultValue) {
        if (bundle.containsKey(key)) {
            return bundle.getInt(key);
        }
        return defaultValue;
    }

    @Override public void setInputObservables(Observable<CharSequence> nameChangeEvents, Observable<CharSequence> dateChangeEvents) {
        Observable<Boolean> validName = nameChangeEvents
                //.doOnNext(t -> System.out.print(String.format("-'%s'-", t.toString())))
                .map(t -> t.length())
                .map(l -> l > 0);

        Observable<Boolean> validDate = dateChangeEvents
                //.doOnNext(t -> System.out.print(String.format("-'%s'-", t.toString())))
                .map(t -> t.length())
                .map(l -> l > 0);


        Observable<Boolean> enableSaveButton = Observable.combineLatest(validName, validDate, (a, b) -> a && b);
                //.doOnNext(b -> System.out.print(b));

        subcription.add(nameChangeEvents
                .subscribe(n -> {
                    builder.setName(n.toString());
                }));

        subcription.add(enableSaveButton.distinctUntilChanged()
                .subscribe(enabled -> fragmentView.enableSaveButton(enabled)));

    }

    @Override public void saveInstanceState(Bundle outState) {
        builder.archiveTo(outState);
    }

    @Override public void setDate(int year, @Date.Month int month, int day) {
        fragmentView.showDate(dateFormatter.format(year, month, day));
        builder.setYear(year).setMonth(month).setDay(day);
    }

    @Override public void setDate(@Date.Month int month, int day) {
        fragmentView.showDate(dateFormatter.format(month, day));
        builder.clearYear().setMonth(month).setDay(day);
    }

    @Override public void saveEvent() {
        IEvent event = builder.build();
        if (event != null) {
            fragmentView.showSavedEvent(event);
            transaction.add(event).commit();
        }
        else {
            fragmentView.showNothingSaved();
        }
    }
}
