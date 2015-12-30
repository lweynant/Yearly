package com.lweynant.yearly;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.lweynant.yearly.controller.EventsActivity;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.ui.EventViewFactory;
import com.lweynant.yearly.ui.IEventNotificationText;
import com.lweynant.yearly.util.IClock;

import org.joda.time.LocalDate;

import rx.Subscriber;
import timber.log.Timber;

public class EventNotifier extends Subscriber<IEvent> {
    private final IClock clock;
    private final EventViewFactory viewFactory;
    private Context context;

    public EventNotifier(EventViewFactory viewFactory, Context context, IClock clock) {
        this.viewFactory = viewFactory;
        this.clock = clock;
        this.context = context;
    }

    @Override
    public void onCompleted() {
        Timber.d("onCompleted");

    }

    @Override
    public void onError(Throwable e) {
        Timber.e(e, "onError");

    }

    @Override
    public void onNext(IEvent event) {
        Timber.d("onNext %s", event.toString());
        Timber.d("sending notification for %s using id %d", event.getName(), event.getID());
        LocalDate now = clock.now();
        LocalDate eventDate = event.getDate();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_cake_white_48dp);
        IEventNotificationText notifText = viewFactory.getEventNotificationText(event);
        String title = notifText.getTitle();
        String subTitle = notifText.getText();
        builder.setContentTitle(title);
        builder.setContentText(subTitle);
        builder.setAutoCancel(true);
        Intent intent = new Intent(context, EventsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(event.getID(), builder.build());
    }
}
