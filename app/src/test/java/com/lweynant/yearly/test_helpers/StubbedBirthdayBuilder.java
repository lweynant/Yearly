package com.lweynant.yearly.test_helpers;

import android.os.Bundle;

import com.lweynant.yearly.model.Birthday;
import com.lweynant.yearly.model.BirthdayBuilder;
import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.IEventID;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IEventNotificationText;
import com.lweynant.yearly.ui.IEventViewFactory;

import org.joda.time.LocalDate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.when;

public class StubbedBirthdayBuilder {
    public static Birthday stubBuilderAndBundleForEvent(BirthdayBuilder builder, Bundle args, String name, @Date.Month int month, int day, String sharedText, IEventViewFactory eventViewFactory, IClock clock) {
        when(builder.canBuild()).thenReturn(true);

        Birthday birthday = mock(Birthday.class);
        when(builder.build()).thenReturn(birthday);
        when(birthday.getName()).thenReturn(name);
        LocalDate date = new LocalDate(clock.now().getYear(), month, day);

        IEventNotificationText text = mock(IEventNotificationText.class);
        when(text.getOneLiner()).thenReturn(sharedText);
        when(eventViewFactory.getEventNotificationText(birthday)).thenReturn(text);
        when(birthday.getDate()).thenReturn(date);
        return birthday;
    }
    public static void stubBuilderAndBundleForEvent(BirthdayBuilder builder, Bundle args, String name, int year, @Date.Month int month, int day, String sharedText, IEventViewFactory eventViewFactory, IClock clock) {
        Birthday birthday = stubBuilderAndBundleForEvent(builder, args, name, month, day, sharedText, eventViewFactory, clock);
        when(birthday.hasYearOfOrigin()).thenReturn(true);
        when(birthday.getYearOfOrigin()).thenReturn(year);
    }
}
