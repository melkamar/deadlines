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
    URGENCY_ASC, URGENCY_DESC
}