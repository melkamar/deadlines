/*
 * Copyright (c) 2016 Martin Melka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.melkamar.deadlines.unit;

import com.melkamar.deadlines.utils.DateConvertor;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.Assert.assertTrue;

/**
 @author Martin Melka
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