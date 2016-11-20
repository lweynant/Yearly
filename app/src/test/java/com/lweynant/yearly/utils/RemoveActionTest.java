package com.lweynant.yearly.utils;

import com.lweynant.yearly.model.Birthday;
import com.lweynant.yearly.model.Event;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.ITransaction;
import com.lweynant.yearly.platform.IEventNotification;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RemoveActionTest {
    @Mock ITransaction transaction;
    @Mock IEventNotification eventNotification;
    @Mock IEvent event;
    @Before public void setUp() {
        when(event.getID()).thenReturn(3);
        when(transaction.remove(any(IEvent.class))).thenReturn(transaction);
    }
    @Test public void remove() {
        RemoveAction sut = new RemoveAction(transaction, eventNotification);
        sut.remove(event);

        verify(transaction).remove(event);
        verify(transaction).commit();
        verify(eventNotification).cancel(event.getID());
    }
}
