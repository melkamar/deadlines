package com.melkamar.deadlines.controllers.requestobjects;

import java.util.List;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 10.04.2016 15:58
 */
public class MembershipOfferRequestBody {
    private List<Long> userIds;

    public List<Long> getUserIds() {
        return userIds;
    }
}