package com.lweynant.yearly.controller.add_event;

import android.os.Bundle;

import com.lweynant.yearly.controller.DateFormatter;
import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.EventBuilder;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.ITransaction;

import rx.Observable;
import rx.subscriptions.CompositeSubscription;

public class AddEventPresenter implements AddEventContract.UserActionListener{

    private AddEventContract.FragmentView fragmentView;
    private EventBuilder builder;
    private ITransaction transaction;
    private DateFormatter dateFormatter;
    private CompositeSubscription subcription = new CompositeSubscription();

    public AddEventPresenter(EventBuilder builder, ITransaction transaction, DateFormatter dateFormatter){
        this.builder = builder;
        this.transaction = transaction;
        this.dateFormatter = dateFormatter;
    }

    @Override
    public void restoreFromInstanceState(AddEventContract.FragmentView fragmentView, Bundle savedInstanceState) {
        this.fragmentView = fragmentView;
        if (savedInstanceState != null) {
            builder.set(savedInstanceState);
        }
    }

    @Override public void setDate(int year, @Date.Month int month, int day) {
        fragmentView.showDate(dateFormatter.format(year, month, day));
        builder.setYear(year).setMonth(month).setDay(day);
    }

    @Override
    public void setInputObservables(Observable<CharSequence> nameChangeEvents, Observable<CharSequence> dateChangeEvents) {
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


    @Override public void setDate(@Date.Month int month, int day) {
        fragmentView.showDate(dateFormatter.format(month, day));
        builder.clearYear().setMonth(month).setDay(day);
    }

    @Override public void saveEvent() {
        IEvent event = builder.build();
        if (event != null) {
            fragmentView.showSavedEvent(event.getName());
            transaction.add(event).commit();
        }
        else {
            fragmentView.showNothingSaved();
        }
    }

    @Override public void saveInstanceState(Bundle outState) {
        builder.archiveTo(outState);
    }
}
