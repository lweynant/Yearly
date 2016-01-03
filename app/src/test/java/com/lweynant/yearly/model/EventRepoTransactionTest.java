package com.lweynant.yearly.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EventRepoTransactionTest {

    @Mock IEventRepoModifier repoModifier;
    private EventRepoTransaction sut;

    @Before public void setUp() {
        sut = new EventRepoTransaction(repoModifier);
    }


    @Test public void testCommitEmptyTransaction() {
        sut.commit();
        verify(repoModifier, never()).commit(sut);
    }

    @Test public void testCommitAfterAdd() {
        sut.add(createAnEvent());
        sut.commit();
        verify(repoModifier, times(1)).commit(sut);
    }

    @Test public void testCommitTwiceAfterAdd() {
        sut.add(createAnEvent());
        sut.commit();
        sut.commit();
        verify(repoModifier, times(1)).commit(sut);
    }

    @Test public void testCommitAfterRemove() {
        sut.remove(createAnEvent());
        sut.commit();
        verify(repoModifier, times(1)).commit(sut);
    }

    @Test public void testCommitTwiceAfterRemove() {
        sut.remove(createAnEvent());
        sut.commit();
        sut.commit();
        verify(repoModifier, times(1)).commit(sut);
    }

    @Test public void testAddOnEmptyTransaction() {
        List<IEvent> list = sut.add().toList().toBlocking().first();
        assertThat(list, hasSize(0));
    }

    @Test public void testAddOnTransactionWithOneEventAdded() {
        IEvent event = createAnEvent();
        sut.add(event);
        List<IEvent> list = sut.add().toList().toBlocking().first();
        assertThat(list, contains(event));
    }

    @Test public void testAddOnTransactionWithNEventAdded() {
        IEvent event1 = createAnEvent();
        IEvent event2 = createAnEvent();
        sut.add(event1).add(event2);
        List<IEvent> list = sut.add().toList().toBlocking().first();
        assertThat(list, containsInAnyOrder(event1, event2));
    }

    @Test public void testAddOnCommittedTransaction() {
        sut.add(createAnEvent()).add(createAnEvent()).add(createAnEvent()).remove(createAnEvent());
        sut.commit();
        List<IEvent> list = sut.add().toList().toBlocking().first();
        assertThat(list, hasSize(0));
    }

    @Test public void testRemoveOnEmptyTransaction() {
        List<IEvent> list = sut.remove().toList().toBlocking().first();
        assertThat(list, hasSize(0));
    }
    @Test public void testRemoveOnTransactionWithOneEventRevoved() {
        IEvent event = createAnEvent();
        sut.remove(event);
        List<IEvent> list = sut.remove().toList().toBlocking().first();
        assertThat(list, contains(event));
    }

    @Test public void testRemoveOnTransactionWithNEventsRemoved() {
        IEvent event1 = createAnEvent();
        IEvent event2 = createAnEvent();
        sut.remove(event1).remove(event2);
        List<IEvent> list = sut.remove().toList().toBlocking().first();
        assertThat(list, containsInAnyOrder(event1, event2));
    }

    @Test public void testRemoveOnCommittedTransaction() {
        sut.remove(createAnEvent()).remove(createAnEvent()).add(createAnEvent()).remove(createAnEvent());
        sut.commit();
        List<IEvent> list = sut.remove().toList().toBlocking().first();
        assertThat(list, hasSize(0));
    }

    private IEvent createAnEvent() {
        return mock(IEvent.class);
    }
}
