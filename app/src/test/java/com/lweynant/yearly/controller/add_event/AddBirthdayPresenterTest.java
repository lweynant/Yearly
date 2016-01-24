package com.lweynant.yearly.controller.add_event;

import android.os.Bundle;

import com.lweynant.yearly.IStringResources;
import com.lweynant.yearly.R;
import com.lweynant.yearly.controller.DateFormatter;
import com.lweynant.yearly.model.Birthday;
import com.lweynant.yearly.model.BirthdayBuilder;
import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.IEventRepoTransaction;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IUniqueIdGenerator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddBirthdayPresenterTest {

    @Mock IClock clock;
    @Mock IUniqueIdGenerator idGenerator;
    @Mock DateFormatter dateFormatter;
    private AddBirthdayPresenter sut;
    @Mock AddBirthdayContract.FragmentView fragmentView;
    @Mock IStringResources rstring;

    String[] months = new String[] {"%1$d", "%1$d jan", "%1$d feb", "%1$d mar", "%1$d apr", "%1$d mei", "%1$d jun", "%1$d jul", "%1$d aug", "%1$d sep", "%1$d okt", "%1$d nov", "%1$d dec" };
    @Mock IEventRepoTransaction transaction;
    @Mock BirthdayBuilder birthdayBuilder;


    @Before public void setUp() {
        //transaction and birthday builder have fluent interface - make sure we return itself
        when(transaction.add(anyObject())).thenReturn(transaction);
        when(birthdayBuilder.setDay(anyInt())).thenReturn(birthdayBuilder);
        //noinspection ResourceType
        when(birthdayBuilder.setMonth(anyInt())).thenReturn(birthdayBuilder);
        when(birthdayBuilder.setYear(anyInt())).thenReturn(birthdayBuilder);
        when(birthdayBuilder.clearYear()).thenReturn(birthdayBuilder);
        when(birthdayBuilder.setName(anyString())).thenReturn(birthdayBuilder);
        when(birthdayBuilder.setLastName(anyString())).thenReturn(birthdayBuilder);

        when(rstring.getStringArray(R.array.months_day)).thenReturn(months);
        when(rstring.getString(eq(R.string.yyy_mm_dd), anyInt(), anyInt(), anyInt() )).thenReturn("formatted date");
        sut = new AddBirthdayPresenter(birthdayBuilder, transaction, new DateFormatter(rstring));
        sut.restoreFromInstanceState(fragmentView, null);
    }

    @Test public void setName() {
        sut.setInputObservables(Observable.just("Joe"), Observable.empty(), Observable.empty());

        verify(birthdayBuilder).setName("Joe");

    }
    @Test public void setLastName() {
        sut.setInputObservables(Observable.empty(), Observable.just("Doe"), Observable.empty());

        verify(birthdayBuilder).setLastName("Doe");
    }

    @Test public void onlyDateWithoutYear() {
        sut.setDate(Date.DECEMBER, 23);

        verify(birthdayBuilder).setMonth(Date.DECEMBER);
        verify(birthdayBuilder).setDay(23);
        verify(birthdayBuilder).clearYear();
    }

    @Test public void onlyDateWithYear() {
        sut.setDate(2015, Date.DECEMBER, 23);

        verify(birthdayBuilder).setMonth(Date.DECEMBER);
        verify(birthdayBuilder).setDay(23);
        verify(birthdayBuilder).setYear(2015);

    }

    @Test public void saveBirthday() {
        Birthday birthday = createBirthday("Joe");
        when(birthdayBuilder.build()).thenReturn(birthday);

        sut.saveBirthday();

        verify(fragmentView).showSavedBirthday("Joe");
        verify(transaction).add(birthday);
        verify(transaction).commit();
    }

    @Test public void saveNothing() {
        when(birthdayBuilder.build()).thenReturn(null);

        sut.saveBirthday();

        verify(fragmentView).showNothingSaved();
        verifyZeroInteractions(transaction);
    }

    private Birthday createBirthday(String name) {
        Birthday bd = mock(Birthday.class);
        when(bd.getName()).thenReturn(name);
        return bd;
    }

    @Test public void showDateWithoutYear() {
        sut.setDate(Date.DECEMBER, 23);

        verify(fragmentView).showDate("23 dec");
    }
    @Test public void showDateWithYear() {
        sut.setDate(2015, Date.DECEMBER, 23);

        verify(fragmentView).showDate("formatted date");
    }

    @Test public void saveButtonEnabledIfNameAndDateFilled() {
        sut.setInputObservables(Observable.just("Joe"), Observable.empty(), Observable.just("23 dec"));

        verify(fragmentView).enableSaveButton(true);
    }

//    @Test public void saveButtonDisnabledIfNameCleared() {
//        CharSequence date = "23 dec";
//        sut.setInputObservables(Observable.from(new String[]{"Joe", "", "Fred"}), Observable.empty(),
//                                Observable.from(new String[]{"Dec", "Jan", "Feb"}));
//        verify(fragmentView, times(3)).enableSaveButton(anyBoolean());
//        InOrder inOrder = inOrder(fragmentView, fragmentView);
//        inOrder.verify(fragmentView).enableSaveButton(true);
//        inOrder.verify(fragmentView).enableSaveButton(false);
//        inOrder.verify(fragmentView).enableSaveButton(true);
//    }
    @Test public void saveButtonNotEnabledIfNameOnlyFilled() {
        sut.setInputObservables(Observable.just("Joe"), Observable.empty(), Observable.empty());

        verify(fragmentView, never()).enableSaveButton(true);
    }

    @Test public void restoreFromState() {
        Bundle state = mock(Bundle.class);
        sut.restoreFromInstanceState(fragmentView, state);

        verify(birthdayBuilder).set(state);
    }
    @Test public void saveInstanceState() {
        Bundle birthdayBundle = mock(Bundle.class);
        sut.saveInstanceState(birthdayBundle);

        verify(birthdayBuilder).archiveTo(birthdayBundle);
    }


}
