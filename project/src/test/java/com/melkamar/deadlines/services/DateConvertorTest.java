package com.melkamar.deadlines.services;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 11.04.2016 11:22
 *
 * Check that conversion Date <--> LocalDateTime yields expected results.
 * Compare the converted object to an original created in the same time and see
 * if their times are "close enough" together.
 */
public class DateConvertorTest {

    @Test
    public void localDateTimeToDate() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.now();
        Date date = new Date();

        Date converted = DateConvertor.localDateTimeToDate(localDateTime);

        System.out.println("LocalDateTime: "+localDateTime);
        System.out.println("Date:          "+date);
        System.out.println("DateConverted: "+converted);

        assertTrue(Math.abs(date.getTime()-converted.getTime()) < 1000);
    }

    @Test
    public void dateToLocalDateTime() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.now();
        Date date = new Date();

        LocalDateTime converted = DateConvertor.dateToLocalDateTime(date);

        System.out.println("Date:          "+date);
        System.out.println("LocalDateTime: "+localDateTime);
        System.out.println("LDTConverted:  "+converted);

        assertTrue(Math.abs(localDateTime.until(converted, ChronoUnit.MILLIS)) < 1000);
    }
}