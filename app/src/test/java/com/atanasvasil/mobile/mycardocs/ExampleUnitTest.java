package com.atanasvasil.mobile.mycardocs;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void dateTest() throws ParseException {
        String dobText = "20.05.2021";
        Date dob = new SimpleDateFormat("dd.MM.yyyy").parse(dobText);
        System.out.println(dob);

        String currentDateText = "10.05.2021";
        Date currentDate = new SimpleDateFormat("dd.MM.yyyy").parse(currentDateText);
        System.out.println(currentDate);

        long diff = dob.getTime() - currentDate.getTime();

        int days = (int) (diff / (1000 * 60 * 60 * 24));
        System.out.println(days);
    }
}