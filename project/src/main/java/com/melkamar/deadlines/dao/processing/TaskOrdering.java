package com.melkamar.deadlines.dao.processing;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 01.04.2016 14:49
 */
public enum TaskOrdering {
    NONE,
    NAME_ASC, NAME_DESC,
    DATE_START_ASC, DATE_START_DESC,
    DATE_DEADLINE_ASC, DATE_DEADLINE_DESC,
    WORKED_PERCENT_ASC, WORKED_PERCENT_DESC,
    PRIORITY_ASC, PRIORITY_DESC,
    URGENCY_ASC, URGENCY_DESC;

    public static final String STR_NAME = "name";
    public static final String STR_DATE_START = "date";
    public static final String STR_DATE_DEADLINE = "deadline";
    public static final String STR_WORKED_PERCENT = "worked";
    public static final String STR_PRIORITY = "priority";
    public static final String STR_URGENCY = "urgency";
}