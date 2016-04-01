package com.melkamar.deadlines.dao.offer;

import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.offer.Offer;
import com.melkamar.deadlines.model.task.Task;

import java.util.Set;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 01.04.2016 18:40
 */
public interface OfferDAO {
    public Offer save(Offer offer);
    public Set<Offer> findAll();
}
