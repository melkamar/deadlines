package com.melkamar.deadlines.services;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 27.03.2016 15:00
 */
public class DateConvertor {
    public static Date localDateTimeToDate(LocalDateTime localDateTime){
        if (localDateTime == null) return null;
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDateTime dateToLocalDateTime(Date date){
        if (date == null) return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

}
