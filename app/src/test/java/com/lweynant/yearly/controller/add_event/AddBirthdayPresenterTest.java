package com.lweynant.yearly.controller.add_event;

import android.os.Bundle;

import com.lweynant.yearly.controller.DateFormatter;
import com.lweynant.yearly.model.Birthday;
import com.lweynant.yearly.model.BirthdayBuilder;
import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.ITransaction;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IPictureRepo;
import com.lweynant.yearly.platform.IUniqueIdGenerator;
import com.lweynant.yearly.test_helpers.StubbedBundle;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;

import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddBirthdayPresenterTest {

    @Mock IClock clock;
    @Mock IUniqueIdGenerator idGenerator;
    //@Mock DateFormatter dateFormatter;
    private AddBirthdayPresenter sut;
    @Mock AddBirthdayContract.FragmentView fragmentView;

    @Mock ITransaction transaction;
    @Mock BirthdayBuilder birthdayBuilder;
    private LocalDate today;
    @Mock Bundle emptyBundle;
    @Mock DateFormatter dateFormatter;
    @Mock IPictureRepo pictureRepo;


    @Before public void setUp() {
        today = new LocalDate(2016, Date.FEBRUARY, 20);
        when(clock.now()).thenReturn(today);
        //transaction and birthday builder have fluent interface - make sure we return itself
        when(transaction.add(anyObject())).thenReturn(transaction);
        when(birthdayBuilder.setDay(anyInt())).thenReturn(birthdayBuilder);
        //noinspection ResourceType
        when(birthdayBuilder.setMonth(anyInt())).thenReturn(birthdayBuilder);
        when(birthdayBuilder.setYear(anyInt())).thenReturn(birthdayBuilder);
        when(birthdayBuilder.clearYear()).thenReturn(birthdayBuilder);
        when(birthdayBuilder.setName(anyString())).thenReturn(birthdayBuilder);
        when(birthdayBuilder.setLastName(anyString())).thenReturn(birthdayBuilder);

        //noinspection ResourceType
        when(dateFormatter.format(anyInt(), anyInt())).thenReturn("formatted_mm_dd");
        when(dateFormatter.format(anyInt(), anyInt(), anyInt())).thenReturn("formatted_yy_mm_dd");
        sut = new AddBirthdayPresenter(birthdayBuilder, transaction, pictureRepo, dateFormatter, clock);
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
        sut.initialize(fragmentView, emptyBundle);
        sut.setDate(Date.DECEMBER, 23);

        verify(birthdayBuilder).setMonth(Date.DECEMBER);
        verify(birthdayBuilder).setDay(23);
        verify(birthdayBuilder).clearYear();
    }

    @Test public void onlyDateWithYear() {
        sut.initialize(fragmentView, emptyBundle);
        sut.setDate(2015, Date.DECEMBER, 23);

        verify(birthdayBuilder).setMonth(Date.DECEMBER);
        verify(birthdayBuilder).setDay(23);
        verify(birthdayBuilder).setYear(2015);

    }

    @Test public void saveBirthday() {
        sut.initialize(fragmentView, emptyBundle);
        Birthday birthday = createBirthday("Joe");
        when(birthdayBuilder.build()).thenReturn(birthday);

        sut.saveBirthday();

        verify(fragmentView).showSavedBirthday(birthday);
        verify(fragmentView, never()).showPicture(any());
        verify(transaction).add(birthday);
        verify(transaction).commit();
        verifyZeroInteractions(pictureRepo);
    }
    @Test public void saveBirthdayWithPicture() {
        sut.initialize(fragmentView, emptyBundle);
        Birthday birthday = createBirthday("Joe");
        when(birthdayBuilder.build()).thenReturn(birthday);
        File picture = new File("picture");
        when(pictureRepo.getPicture()).thenReturn(picture);
        sut.takePicture();
        sut.setPicture();
        sut.saveBirthday();

        verify(fragmentView).showSavedBirthday(birthday);
        verify(fragmentView).showPicture(picture);
        verify(transaction).add(birthday);
        verify(transaction).commit();
        verify(pictureRepo).storePicture(birthday, picture);
    }
    @Test public void saveBirthdayWithCancelledPicture() {
        sut.initialize(fragmentView, emptyBundle);
        Birthday birthday = createBirthday("Joe");
        when(birthdayBuilder.build()).thenReturn(birthday);
        File picture = new File("picture");
        when(pictureRepo.getPicture()).thenReturn(picture);
        sut.takePicture();
        sut.clearPicture();
        sut.saveBirthday();

        verify(fragmentView).showSavedBirthday(birthday);
        verify(fragmentView, never()).showPicture(any());
        verify(transaction).add(birthday);
        verify(transaction).commit();
        verify(pictureRepo, never()).storePicture(any(), any());
    }

    @Test public void saveNothing() {
        sut.initialize(fragmentView, emptyBundle);
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
        sut.initialize(fragmentView, emptyBundle);
        sut.setDate(Date.DECEMBER, 23);

        verify(fragmentView).showDate("formatted_mm_dd");
    }
    @Test public void showDateWithYear() {
        sut.initialize(fragmentView, emptyBundle);
        sut.setDate(2015, Date.DECEMBER, 23);

        verify(fragmentView).showDate("formatted_yy_mm_dd");
    }

    @Test public void saveButtonEnabledIfNameAndDateFilled() {
        sut.initialize(fragmentView, emptyBundle);
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
        sut.initialize(fragmentView, emptyBundle);
        sut.setInputObservables(Observable.just("Joe"), Observable.empty(), Observable.empty());

        verify(fragmentView, never()).enableSaveButton(true);
    }

    @Test public void restoreFromState() {
        Bundle state = mock(Bundle.class);
        sut.initialize(fragmentView, state);

        verify(birthdayBuilder).set(state);
    }
    @Test public void saveInstanceState() {
        Bundle birthdayBundle = mock(Bundle.class);
        sut.saveInstanceState(birthdayBundle);

        verify(birthdayBuilder).archiveTo(birthdayBundle);
    }


    @Test public void initializeWithEmptyArg() {
        //when(dateFormatter.format(today.getMonthOfYear(), today.getDayOfMonth())).thenReturn("formatted date");
        Bundle emptyArgs = mock(Bundle.class);
        sut.initialize(fragmentView, emptyArgs);

        verify(fragmentView).initialize(null, null, null, today.getYear(), today.getMonthOfYear(), today.getDayOfMonth(), null);
    }

    @Test public void initializeWithValidEventArgWithYear() {
        when(dateFormatter.format(2001, Date.APRIL, 23)).thenReturn("formatted date");
        Bundle args = createArgsFor("Events name", 2001, Date.APRIL, 23);
        sut.initialize(fragmentView, args);

        verify(fragmentView).initialize(("Events name"), null, ("formatted date"), 2001, Date.APRIL, 23, null);
    }

    @Test public void initializeWithValidEventArg() {
        when(dateFormatter.format(Date.APRIL, 23)).thenReturn("the date");
        Bundle args = createArgsFor("Events name", Date.APRIL, 23);
        sut.initialize(fragmentView, args);

        verify(fragmentView).initialize("Events name", null, "the date", today.getYear(), Date.APRIL, 23, null);
    }


    private Bundle createArgsFor(String name, int month, int day) {
        return StubbedBundle.createBundleForEvent(name, month, day);
    }
    private Bundle createArgsFor(String name, int year, int month, int day) {
        return StubbedBundle.createBundleForEvent(name, year, month, day);
    }
}
