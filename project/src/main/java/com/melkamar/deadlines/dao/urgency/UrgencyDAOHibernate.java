package com.melkamar.deadlines.dao.urgency;

import com.melkamar.deadlines.model.task.Urgency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 06.04.2016 21:43
 */
@Service("urgencyDao")
public class UrgencyDAOHibernate implements UrgencyDAO {
    @Autowired
    UrgencyRepository urgencyRepository;

    @Override
    public Urgency save(Urgency urgency) {
        return urgencyRepository.save(urgency);
    }
}
