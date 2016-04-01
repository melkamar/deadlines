package com.melkamar.deadlines.dao.offer;

import com.google.common.collect.Sets;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.offer.Offer;
import com.melkamar.deadlines.model.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 01.04.2016 18:40
 */
@Service("offerDao")
public class OfferDAOHibernate implements OfferDAO {
    @Qualifier("offerRepository")
    @Autowired
    private OfferRepository offerRepository;

    @Override
    public Offer save(Offer offer) {
        return offerRepository.save(offer);
    }

    @Override
    public Set<Offer> findAll() {
        return Sets.newHashSet(offerRepository.findAll());
    }
}
