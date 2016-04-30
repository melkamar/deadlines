/*
 * Copyright (c) 2016 Martin Melka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
