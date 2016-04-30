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

package com.melkamar.deadlines.dao.processing;

/**
 * @author Martin Melka
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