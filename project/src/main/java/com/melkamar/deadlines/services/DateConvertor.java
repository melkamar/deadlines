package com.melkamar.deadlines.services;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 27.03.2016 15:00
 */
@Service
public class DateConvertor {
    public Date localDateTimeToDate(LocalDateTime localDateTime){
        Instant instant = localDateTime.toInstant(ZoneOffset.UTC);
        Date date = Date.from(instant);

        return date;
    }

    public LocalDateTime dateToLocalDateTime(Date date){
        Instant instant = Instant.ofEpochMilli(date.getTime());
        LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);

        return ldt;
    }
}
