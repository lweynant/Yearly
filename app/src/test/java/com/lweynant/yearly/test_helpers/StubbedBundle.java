package com.lweynant.yearly.test_helpers;

import android.os.Bundle;

import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.IEvent;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StubbedBundle {
    static public Bundle createBundleForEvent(String name, @Date.Month int month, int day) {
        Bundle args = mock(Bundle.class);
        stubBundleForEvent(name, month, day, args);
        return args;
    }

    public static void stubBundleForEvent(String name, @Date.Month int month, int day, Bundle args) {
        when(args.containsKey(IEvent.KEY_NAME)).thenReturn(true);
        when(args.getString(IEvent.KEY_NAME)).thenReturn(name);
        when(args.containsKey(IEvent.KEY_MONTH)).thenReturn(true);
        when(args.getInt(IEvent.KEY_MONTH)).thenReturn(month);
        when(args.containsKey(IEvent.KEY_DAY)).thenReturn(true);
        when(args.getInt(IEvent.KEY_DAY)).thenReturn(day);
    }

    static public Bundle createBundleForEvent(String name, int year, @Date.Month int month, int day) {
        Bundle args = createBundleForEvent(name, month, day);
        when(args.containsKey(IEvent.KEY_YEAR)).thenReturn(true);
        when(args.getInt(IEvent.KEY_YEAR)).thenReturn(year);
        return args;
    }
}
