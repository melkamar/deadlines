package com.melkamar.deadlines.dao.offer;

import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.offer.Offer;
import com.melkamar.deadlines.model.task.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 01.04.2016 18:40
 */
@Repository
public interface OfferRepository extends CrudRepository<Offer, Long> {
}
