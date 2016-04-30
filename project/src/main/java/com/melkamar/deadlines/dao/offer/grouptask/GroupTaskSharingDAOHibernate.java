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

package com.melkamar.deadlines.dao.offer.grouptask;

import com.google.common.collect.Sets;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.offer.GroupTaskSharingOffer;
import com.melkamar.deadlines.model.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author Martin Melka
 */
@Service("groupTaskSharingDao")
public class GroupTaskSharingDAOHibernate implements GroupTaskSharingDAO {

    @Autowired
    private GroupTaskSharingRepository groupTaskSharingRepository;

    @Override
    public Set<GroupTaskSharingOffer> findAll() {
        return Sets.newHashSet(groupTaskSharingRepository.findAll());
    }

    @Override
    public GroupTaskSharingOffer findByOfferedToAndTaskOffered(Group group, Task task) {
        return groupTaskSharingRepository.findByOfferedToAndTaskOffered(group, task);
    }

    @Override
    public Set<GroupTaskSharingOffer> findByOfferedTo(Group group) {
        return groupTaskSharingRepository.findByOfferedTo(group);
    }

    @Override
    public GroupTaskSharingOffer findById(Long id) {
        return groupTaskSharingRepository.findById(id);
    }
}
