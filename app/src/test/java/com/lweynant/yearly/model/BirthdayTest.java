package com.lweynant.yearly.model;


import com.lweynant.yearly.IRString;
import com.lweynant.yearly.R;

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
        when(rstring.getStringFromId(R.string.birthday_from)).thenReturn(BIRTHDAY_TITLE);
        Birthday bd = new Birthday("John", 23, Date.APRIL, rstring);
        assertThat(bd.getTitle(), is("John's birthday"));
    }


    @Test
    public  void getDate_ValidBirthday_ReturnsValidDayAndMonth() throws Exception{
        int day = 23;
        @Date.Month int month = Date.FEBRUARY;
        Birthday bd = new Birthday("John", day, month, rstring);
        assertThat(bd.getDay(), is(day));
        assertThat(bd.getMonth(), is(month));
    }
}
