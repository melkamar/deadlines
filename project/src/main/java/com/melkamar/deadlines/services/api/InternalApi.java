package com.melkamar.deadlines.services.api;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 11.04.2016 14:21
 */
public interface InternalApi {
    void updateAllUrgencies();
    void updateAllUrgencies(boolean force);
}