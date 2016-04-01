package com.melkamar.deadlines.dao.offer.membership;

import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.offer.MembershipOffer;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 01.04.2016 20:37
 */
public interface MembershipSharingDAO {
    public MembershipOffer findByOfferedToAndGroup(User user, Group group);
}
