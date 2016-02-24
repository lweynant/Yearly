package com.lweynant.yearly.controller;

import android.app.Application;
import android.content.Context;

import com.lweynant.yearly.IStringResources;
import com.lweynant.yearly.PerApp;
import com.lweynant.yearly.controller.add_event.AddBirthdayContract;
import com.lweynant.yearly.controller.add_event.AddBirthdayPresenter;
import com.lweynant.yearly.controller.add_event.AddEventContract;
import com.lweynant.yearly.controller.add_event.AddEventPresenter;
import com.lweynant.yearly.controller.list_events.EventsAdapter;
import com.lweynant.yearly.controller.list_events.IEventsLoader;
import com.lweynant.yearly.controller.list_events.ListEventsContract;
import com.lweynant.yearly.controller.list_events.ListEventsPresenter;
import com.lweynant.yearly.model.BirthdayBuilder;
import com.lweynant.yearly.model.EventBuilder;
import com.lweynant.yearly.model.ITransaction;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IEventNotification;
import com.lweynant.yearly.ui.IEventViewFactory;

import dagger.Module;
import dagger.Provides;

@Module
public class ControllerModule {

    private final Context appContext;

    public ControllerModule(Application app) {
        this.appContext = app.getApplicationContext();
    }


    @Provides IIntentFactory providesIntentFactory() {
        return new IntentFactory(appContext);
    }

    @Provides DateFormatter providesDateFormatter(IStringResources rstring) {
        return new DateFormatter(rstring);
    }

    @Provides EventsAdapter provideEventsAdapter(IEventViewFactory viewFactory) {
        return new EventsAdapter(viewFactory);
    }
    @Provides @PerApp EventNotifier providesEventNotifier(IEventNotification eventNotification,
                                                          IIntentFactory intentFactory,
                                                          IEventViewFactory viewFactory,
                                                          IClock clock) {

        return new EventNotifier(eventNotification, intentFactory, viewFactory, clock);
    }

    //preseters straddle the activity/fragment - both should use the same, therefor we have singletons
    @Provides @PerApp AddBirthdayContract.UserActionsListener providesAddBirthdayPresenter(BirthdayBuilder builder,
                                                                                           ITransaction transaction,
                                                                                           DateFormatter dateFormatter,
                                                                                           IClock clock) {
        return new AddBirthdayPresenter(builder, transaction, dateFormatter, clock);
    }

    @Provides @PerApp AddEventContract.UserActionListener providesAddEventPresenter(EventBuilder builder,
                                                                                    ITransaction transaction,
                                                                                    DateFormatter dateFormatter,
                                                                                    IClock clock) {
        return new AddEventPresenter(builder, transaction, dateFormatter, clock);
    }
    @Provides @PerApp ListEventsContract.UserActionsListener providesEventsListPresenter(IEventsLoader eventsLoader,
                                                                                         ITransaction transaction,
                                                                                         IEventNotification eventNotification) {
        return new ListEventsPresenter(eventsLoader,  transaction, eventNotification);
    }



}
