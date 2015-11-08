package com.lweynant.yearly;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.model.IEvent;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.List;

import timber.log.Timber;

public class AlarmGenerator {
    private Context context;
    private PendingIntent mAlarmSender;

    public AlarmGenerator(Context context) {
        this.context = context;
        Intent intent = new Intent(context, AlarmReceiver.class);
        mAlarmSender = PendingIntent.getBroadcast(context, 0, intent, 0);
    }



    public void startAlarm(LocalDate now) {
        Timber.d("startAlarm");
        EventRepo repo = EventRepo.getInstance();
        repo.sortFrom(now.getMonthOfYear(), now.getDayOfMonth());
        List<IEvent> events = repo.getUpcomingEvents();
        for (IEvent event : events) {
            LocalDate eventDate = new LocalDate(now.getYear(), event.getMonth(), event.getDay());
            if (eventDate.isBefore(now)) {
                eventDate = eventDate.plusYears(1);
            }
            int morning = 6;
            int evening = 19;
            int hour = eventDate.isEqual(now)? morning : evening;
            LocalDate alarmDate = eventDate.isEqual(now)? eventDate: eventDate.minusDays(1);
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            DateTime time = new DateTime(alarmDate.getYear(), alarmDate.getMonthOfYear(), alarmDate.getDayOfMonth(), hour, 0);
            long triggerAtMillis = time.toDateTime().getMillis();
            Timber.d("shedule an alarm for event %s on date: %s", eventDate.toString(), time.toString());
            am.set(AlarmManager.RTC, triggerAtMillis, mAlarmSender);
        }
    }

}
