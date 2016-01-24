package com.lweynant.yearly.controller.add_event;

import android.os.Bundle;

import com.lweynant.yearly.IStringResources;
import com.lweynant.yearly.R;
import com.lweynant.yearly.controller.DateFormatter;
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
    @Mock Bundle birthdayBundle;
    private AddBirthdayPresenter sut;
    @Mock AddBirthdayContract.FragmentView fragmentView;
    @Mock IStringResources rstring;

    String[] months = new String[] {"%1$d", "%1$d jan", "%1$d feb", "%1$d mar", "%1$d apr", "%1$d mei", "%1$d jun", "%1$d jul", "%1$d aug", "%1$d sep", "%1$d okt", "%1$d nov", "%1$d dec" };
    @Mock IEventRepoTransaction transaction;


    @Before public void setUp() {
        //transaction has fluent interface - make sure we return itself
        when(transaction.add(anyObject())).thenReturn(transaction);
        when(rstring.getStringArray(R.array.months_day)).thenReturn(months);
        when(rstring.getString(eq(R.string.yyy_mm_dd), anyInt(), anyInt(), anyInt() )).thenReturn("formatted date");
        sut = new AddBirthdayPresenter(new BirthdayBuilder(clock, idGenerator), transaction, new DateFormatter(rstring));
        sut.restoreFromInstanceState(fragmentView, null);
    }

    @Test public void noInputs() {
        sut.setInputObservables(Observable.empty(), Observable.empty(), Observable.empty());

        sut.saveBirthday();
        verify(fragmentView).showNothingSaved();
        verifyZeroInteractions(transaction);
    }

    @Test public void onlyName() {
        sut.setInputObservables(Observable.just("j", "jo", "joe"), Observable.empty(), Observable.empty());

        sut.saveBirthday();

        verify(fragmentView).showNothingSaved();
        verifyZeroInteractions(transaction);
    }

    @Test public void onlyLastName() {
        sut.setInputObservables(Observable.empty(), Observable.just("D", "Do", "Doe"), Observable.empty());

        sut.saveBirthday();

        verify(fragmentView).showNothingSaved();
        verifyZeroInteractions(transaction);
    }

    @Test public void onlyDateWithoutYear() {
        sut.setDate(Date.DECEMBER, 23);

        sut.saveBirthday();

        verify(fragmentView).showNothingSaved();
        verifyZeroInteractions(transaction);
    }

    @Test public void onlyDateWithYear() {
        sut.setDate(2015, Date.DECEMBER, 23);

        sut.saveBirthday();

        verify(fragmentView).showNothingSaved();
        verifyZeroInteractions(transaction);
    }

    @Test public void minimalValidData() {
        sut.setInputObservables(Observable.just("Joe"), Observable.empty(), Observable.empty());
        sut.setDate(Date.APRIL, 24);

        sut.saveBirthday();

        verify(fragmentView).showSavedBirthday("Joe");
        verify(transaction).add(anyObject());
        verify(transaction).commit();
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
        Bundle state = createBundle("Joe", Date.APRIL, 25);
        sut.restoreFromInstanceState(fragmentView, state);

        sut.saveBirthday();

        verify(fragmentView).showSavedBirthday("Joe");
        verify(transaction).add(anyObject());
        verify(transaction).commit();
    }
    @Test public void saveInstanceState() {
        Bundle state = createBundle("Fred", Date.MARCH, 25);
        sut.restoreFromInstanceState(fragmentView, state);

        sut.saveInstanceState(birthdayBundle);
        verify(birthdayBundle).putString(BirthdayBuilder.KEY_NAME, "Joe");
        verify(birthdayBundle).putInt(BirthdayBuilder.KEY_DAY, 25);
        verify(birthdayBundle).putInt(BirthdayBuilder.KEY_MONTH, Date.APRIL);
    }

    private Bundle createBundle(String name, int month, int day) {
        Bundle bundle = mock(Bundle.class);
        when(bundle.containsKey(BirthdayBuilder.KEY_NAME)).thenReturn(true);
        when(bundle.getString(BirthdayBuilder.KEY_NAME)).thenReturn("Joe");
        when(bundle.containsKey(BirthdayBuilder.KEY_MONTH)).thenReturn(true);
        when(bundle.getInt(BirthdayBuilder.KEY_MONTH)).thenReturn(Date.APRIL);
        when(bundle.containsKey(BirthdayBuilder.KEY_DAY)).thenReturn(true);
        when(bundle.getInt(BirthdayBuilder.KEY_DAY)).thenReturn(25);
        return bundle;
    }
}
