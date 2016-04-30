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

package com.melkamar.deadlines.controllers.httpbodies;

import java.text.MessageFormat;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 08.04.2016 13:54
 */
@SuppressWarnings("WeakerAccess")
public class ErrorResponse {
    private final int id;
    public final int errorCode;
    public final String errorMessage;

    public ErrorResponse(int errorCode, String errorMessage) {
        this.id = 0;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public ErrorResponse() {
        this.id = 0;
        errorCode = -1;
        errorMessage = null;
    }

    @Override
    public String toString() {
        return MessageFormat.format("'{'\"errorCode\":{0}, \"errorMessage\":\"{1}\"'}'", errorCode+"", errorMessage);
    }
}
