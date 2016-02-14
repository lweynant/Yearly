package com.lweynant.yearly.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import rx.Observable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TransactionTest {

    @Mock IEventRepoModifier repoModifier;
    private Transaction sut;

    @Before public void setUp() {
        sut = new Transaction(repoModifier);
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
    @Test public void testCommitAfterUpdate() {
        sut.update(createAnEvent());
        sut.commit();
        verify(repoModifier, times(1)).commit(sut);
    }
    @Test public void testCommitTwiceAfterUpdate() {
        sut.update(createAnEvent());
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

    @Test public void testAddedOnEmptyTransaction() {
        List<ITransaction.ITransactionItem> list = sut.committed().toList().toBlocking().first();
        assertThat(list, hasSize(0));
    }

    @Test public void testAddedOnTransactionWithOneEventAdded() {
        IEvent event = createAnEvent();
        sut.add(event);
        List<IEvent> list = getEventsList(sut.committed());
        assertThat(list, contains(event));
    }


    @Test public void testAddedOnTransactionWithNEventAdded() {
        IEvent event1 = createAnEvent();
        IEvent event2 = createAnEvent();
        sut.add(event1).add(event2);
        List<IEvent> list = getEventsList(sut.committed());
        assertThat(list, containsInAnyOrder(event1, event2));
    }

    @Test public void testAddedOnCommittedTransaction() {
        sut.add(createAnEvent()).add(createAnEvent()).add(createAnEvent()).remove(createAnEvent());
        sut.commit();
        List<IEvent> list = getEventsList(sut.committed());
        assertThat(list, hasSize(0));
    }

    @Test public void testRemoveOnTransactionWithOneEventRevoved() {
        IEvent event = createAnEvent();
        sut.remove(event);
        List<IEvent> list = getEventsList(sut.committed());
        assertThat(list, contains(event));
    }

    @Test public void testRemoveOnTransactionWithNEventsRemoved() {
        IEvent event1 = createAnEvent();
        IEvent event2 = createAnEvent();
        sut.remove(event1).remove(event2);
        List<IEvent> list = getEventsList(sut.committed());
        assertThat(list, containsInAnyOrder(event1, event2));
    }

    @Test public void testRemoveOnCommittedTransaction() {
        sut.remove(createAnEvent()).remove(createAnEvent()).add(createAnEvent()).remove(createAnEvent());
        sut.commit();
        List<IEvent> list = getEventsList(sut.committed());
        assertThat(list, hasSize(0));
    }

    @Test public void testCommittedOnTransactionWithOneEventUpdated() {
        IEvent event = createAnEvent();
        sut.update(event);
        List<IEvent> list = getEventsList(sut.committed());
        assertThat(list, contains(event));
    }
    @Test public void testCommitedOnTransactionWithNEventsUpdated() {
        IEvent event1 = createAnEvent();
        IEvent event2 = createAnEvent();
        sut.update(event1).update(event2);
        List<IEvent> list = getEventsList(sut.committed());
        assertThat(list, containsInAnyOrder(event1, event2));
    }
    private List<IEvent> getEventsList(Observable<ITransaction.ITransactionItem> committed) {
        return committed.map(t -> t.event()).toList().toBlocking().first();
    }

    private IEvent createAnEvent() {
        return mock(IEvent.class);
    }
}
