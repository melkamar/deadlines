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

package com.melkamar.deadlines.dao.offer.usertask;

import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.offer.UserTaskSharingOffer;
import com.melkamar.deadlines.model.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author Martin Melka
 */
@Service("userTaskSharingDao")
public class UserTaskSharingDAOHibernate implements UserTaskSharingDAO {

    @Autowired
    private UserTaskSharingRepository userTaskSharingRepository;

    @Override
    public UserTaskSharingOffer findByOfferedToAndTaskOffered(User user, Task task) {
        return userTaskSharingRepository.findByOfferedToAndTaskOffered(user, task);
    }

    @Override
    public Set<UserTaskSharingOffer> findByOfferedTo(User user) {
        return userTaskSharingRepository.findByOfferedTo(user);
    }

    @Override
    public UserTaskSharingOffer findById(Long id) {
        return userTaskSharingRepository.findById(id);
    }
}
