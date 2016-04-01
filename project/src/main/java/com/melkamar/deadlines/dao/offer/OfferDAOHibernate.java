package com.melkamar.deadlines.dao.offer;

import com.melkamar.deadlines.model.offer.Offer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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
}
