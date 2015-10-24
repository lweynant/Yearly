package com.lweynant.yearly.model;


import com.lweynant.yearly.IRString;
import com.lweynant.yearly.R;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BirthdayTest {

    private static final String BIRTHDAY_TITLE = "%1$s's birthday";
    @Mock
    IRString rstring;
    @Test
    public void getTitle_ValidBirthday_ReturnsValidTitle() throws Exception{
        when(rstring.getStringFromId(R.string.birthday_title)).thenReturn(BIRTHDAY_TITLE);
        Birthday bd = new Birthday("John", getADate(), rstring);
        assertThat(bd.getTitle(), is("John's birthday"));
    }

    private LocalDate getADate() {
        return new LocalDate(1900, 7, 23);
    }

    @Test
    public  void getDate_ValidBirthday_ReturnsValidDate() throws Exception{
        LocalDate date = new LocalDate(2015, 8, 21);
        Birthday bd = new Birthday("John", date, rstring);
        assertThat(bd.getDate(), is(date));
    }
}
