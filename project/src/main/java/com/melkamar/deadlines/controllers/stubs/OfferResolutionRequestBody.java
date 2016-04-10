package com.melkamar.deadlines.controllers.stubs;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 10.04.2016 11:10
 */
public class OfferResolutionRequestBody {
    @JsonProperty(required = true)
    private Boolean accept;

    public Boolean isAccept() {
        return accept;
    }
}
