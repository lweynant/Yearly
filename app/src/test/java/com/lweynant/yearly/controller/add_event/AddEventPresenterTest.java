package com.lweynant.yearly.controller.add_event;

import com.lweynant.yearly.controller.DateFormatter;
import com.lweynant.yearly.model.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddEventPresenterTest {

    @Mock AddEventContract.FragmentView fragmentView;
    @Mock DateFormatter dateFormatter;
    private AddEventPresenter sut;

    @Before public void setUp() {
        sut = new AddEventPresenter(dateFormatter);
        sut.restoreFromInstanceState(fragmentView, null);
    }

    @Test public void setDate() {
        when(dateFormatter.format(Date.APRIL, 24)).thenReturn("24 april");
        sut.setDate(Date.APRIL, 24);
        verify(fragmentView).showDate("24 april");
    }

    @Test public void setDateWithYear() {
        when(dateFormatter.format(2016, Date.JUNE, 30)).thenReturn("30 juni 2016");
        sut.setDate(2016, Date.JUNE, 30);
        verify(fragmentView).showDate("30 juni 2016");
    }
}
