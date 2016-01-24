package com.lweynant.yearly.controller;

import android.app.Application;
import android.content.Context;

import com.lweynant.yearly.IStringResources;
import com.lweynant.yearly.PerApp;
import com.lweynant.yearly.controller.add_event.AddBirthdayContract;
import com.lweynant.yearly.controller.add_event.AddBirthdayPresenter;
import com.lweynant.yearly.controller.list_events.EventsAdapter;
import com.lweynant.yearly.controller.list_events.IEventsLoader;
import com.lweynant.yearly.controller.list_events.ListEventsContract;
import com.lweynant.yearly.controller.list_events.ListEventsPresenter;
import com.lweynant.yearly.model.BirthdayBuilder;
import com.lweynant.yearly.model.IEventRepoTransaction;
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

    @Provides @PerApp AddBirthdayContract.UserActionsListener providesAddBirthdayPresenter(BirthdayBuilder builder,
                                                                                           DateFormatter dateFormatter) {
        return new AddBirthdayPresenter(builder, dateFormatter);
    }

    @Provides @PerApp ListEventsContract.UserActionsListener provedesEventsListPresenter(IEventsLoader eventsLoader,
                                                                                         IEventRepoTransaction transaction,
                                                                                         IEventNotification eventNotification) {
        return new ListEventsPresenter(eventsLoader,  transaction, eventNotification);
    }



}
