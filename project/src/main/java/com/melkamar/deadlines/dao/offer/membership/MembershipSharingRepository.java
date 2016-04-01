package com.melkamar.deadlines.dao.offer.membership;

import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.offer.MembershipOffer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 01.04.2016 20:37
 */
@Repository
public interface MembershipSharingRepository extends CrudRepository<MembershipOffer, Long> {
    public MembershipOffer findByOfferedToAndGroup(User user, Group group);
    public Set<MembershipOffer> findByOfferedTo(User user);
}
