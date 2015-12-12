package com.lweynant.yearly;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.lweynant.yearly.controller.EventsActivity;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.util.IClock;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import rx.Subscriber;
import timber.log.Timber;

public class EventNotifier extends Subscriber<IEvent> {
    private final IClock clock;
    private Context context;

    public EventNotifier(Context context, IClock clock)
    {
        this.context = context;
        this.clock = clock;
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
        Timber.d("sending notification for %s using id %d", event.getTitle(), event.getID());
        LocalDate now = clock.now();
        LocalDate eventDate = event.getDate();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_event_note_white_48dp);
        String title = event.getTitle();
        String subTitle;
        if (eventDate.isEqual(now))
        {
            subTitle = context.getResources().getString(R.string.today);
        }
        else if (eventDate.minusDays(1).isEqual(now)){
            subTitle = context.getResources().getString(R.string.tomorrow);
        }
        else {
            Days d = Days.daysBetween(now, eventDate);
            int days = d.getDays();
            subTitle = String.format(context.getResources().getString(R.string.in_x_days), days);
        }
        builder.setContentTitle(title);
        builder.setContentText(subTitle);
        builder.setAutoCancel(true);
        Intent intent = new Intent(context, EventsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(event.getID(), builder.build());
    }
}
