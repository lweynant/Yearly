package com.lweynant.yearly.controller;

import android.app.Application;
import android.content.Context;

import com.lweynant.yearly.IStringResources;
import com.lweynant.yearly.PerApp;
import com.lweynant.yearly.controller.add_event.AddBirthdayContract;
import com.lweynant.yearly.controller.add_event.AddBirthdayPresenter;
import com.lweynant.yearly.controller.add_event.AddEventContract;
import com.lweynant.yearly.controller.add_event.AddEventPresenter;
import com.lweynant.yearly.controller.archive.CreateRestoreBackupFileIntentSenderAction;
import com.lweynant.yearly.controller.archive.CreateSaveBackupFileIntentSenderAction;
import com.lweynant.yearly.controller.archive.CreateBackupFolderAction;
import com.lweynant.yearly.controller.archive.HasBackupFolderAction;
import com.lweynant.yearly.controller.list_events.EventsAdapter;
import com.lweynant.yearly.controller.list_events.GroupEventsByDate;
import com.lweynant.yearly.controller.list_events.IEventsLoader;
import com.lweynant.yearly.controller.list_events.IGroupEventsStrategy;
import com.lweynant.yearly.controller.list_events.IListItemsObservable;
import com.lweynant.yearly.controller.list_events.ListEventsContract;
import com.lweynant.yearly.controller.list_events.ListEventsPresenter;
import com.lweynant.yearly.controller.list_events.ListItemsObservable;
import com.lweynant.yearly.controller.show_event.ShowBirthdayContract;
import com.lweynant.yearly.controller.show_event.ShowBirthdayPresenter;
import com.lweynant.yearly.model.BirthdayBuilder;
import com.lweynant.yearly.model.EventBuilder;
import com.lweynant.yearly.model.ITransaction;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.IDateFormatter;
import com.lweynant.yearly.platform.IEventNotification;
import com.lweynant.yearly.ui.IEventViewFactory;
import com.lweynant.yearly.utils.RemoveAction;

import dagger.Module;
import dagger.Provides;

@Module
public class ControllerModule {

    private final Context appContext;

    public ControllerModule(Application app) {
        this.appContext = app.getApplicationContext();
    }

    @Provides RemoveAction providesRemoveAction(ITransaction transaction, IEventNotification eventNotification){
        return new RemoveAction(transaction, eventNotification);
    }


    @Provides IIntentFactory providesIntentFactory() {
        return new IntentFactory(appContext);
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

    @Provides AddBirthdayContract.UserActionsListener providesAddBirthdayPresenter(BirthdayBuilder builder,
                                                                                           ITransaction transaction,
                                                                                           IDateFormatter dateFormatter,
                                                                                           IClock clock) {
        return new AddBirthdayPresenter(builder, transaction, dateFormatter, clock);
    }

    @Provides AddEventContract.UserActionListener providesAddEventPresenter(EventBuilder builder,
                                                                                    ITransaction transaction,
                                                                                    IDateFormatter dateFormatter,
                                                                                    IClock clock) {
        return new AddEventPresenter(builder, transaction, dateFormatter, clock);
    }
    @Provides IGroupEventsStrategy providesGroupEventsStrategy(IClock clock, IStringResources stringResources) {
        return  new GroupEventsByDate(clock, stringResources);
    }
    @Provides IListItemsObservable providesListItemsObservable(IGroupEventsStrategy groupEventsStrategy){
        return new ListItemsObservable(groupEventsStrategy);
    }
    //preseters straddle the activity/fragment - both should use the same, therefor we have singletons
    @Provides @PerApp ShowBirthdayContract.UserActionsListener providesShowBirthdayPresenter(IDateFormatter dateFormatter,
                                                                                     BirthdayBuilder builder,
                                                                                     RemoveAction removeAction,
                                                                                     IEventViewFactory eventViewFactory, IClock clock) {
        return new ShowBirthdayPresenter(dateFormatter, builder, removeAction, eventViewFactory, clock);
    }
    @Provides @PerApp ListEventsContract.UserActionsListener providesEventsListPresenter(IEventsLoader eventsLoader,
                                                                                         IListItemsObservable listItemsFactory,
                                                                                         RemoveAction removeAction) {
        return new ListEventsPresenter(eventsLoader,  listItemsFactory, removeAction);
    }


    @Provides HasBackupFolderAction providesHasBackupFolderAction(){
        return new HasBackupFolderAction();
    }
    @Provides CreateBackupFolderAction providesCreateBackupFolderAction(HasBackupFolderAction hasBackupFolderAction) {
        return new CreateBackupFolderAction(hasBackupFolderAction);
    }
    @Provides
    CreateSaveBackupFileIntentSenderAction providesCreateBackupFileIntentSenderAction(CreateBackupFolderAction createBackupFolderAction,
                                                                                      IStringResources stringResources){
        return new CreateSaveBackupFileIntentSenderAction(createBackupFolderAction, stringResources);
    }
    @Provides CreateRestoreBackupFileIntentSenderAction providesCreateRestoreBackupFileIntentSenderAction(HasBackupFolderAction hasBackupFolderAction, IStringResources stringResources){
        return new CreateRestoreBackupFileIntentSenderAction(hasBackupFolderAction, stringResources);
    }
}
