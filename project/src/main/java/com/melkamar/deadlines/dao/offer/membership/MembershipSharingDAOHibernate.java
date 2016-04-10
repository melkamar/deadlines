package com.melkamar.deadlines.dao.offer.membership;

import com.google.common.collect.Sets;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.offer.MembershipOffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 01.04.2016 20:37
 */
@Service("membershipSharingDao")
public class MembershipSharingDAOHibernate implements MembershipSharingDAO {


    @Qualifier("membershipSharingRepository")
    @Autowired
    private MembershipSharingRepository membershipSharingRepository;

    @Override
    public Set<MembershipOffer> findAll() {
        return Sets.newHashSet(membershipSharingRepository.findAll());
    }

    @Override
    public MembershipOffer findByOfferedToAndGroup(User user, Group group) {
        return membershipSharingRepository.findByOfferedToAndGroup(user, group);
    }

    @Override
    public Set<MembershipOffer> findByOfferedTo(User user) {
        return membershipSharingRepository.findByOfferedTo(user);
    }

    @Override
    public MembershipOffer findById(Long id) {
        return membershipSharingRepository.findById(id);
    }
}
