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

package com.melkamar.deadlines.dao.group;

import com.google.common.collect.Lists;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.MemberRole;
import com.melkamar.deadlines.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 26.03.2016 12:20
 */

@Service("groupDAO")
public class GroupDAOHibernate implements GroupDAO {
    @Autowired
    private GroupRepository groupRepository;

    @Override
    public long count() {
        return groupRepository.count();
    }

    @Override
    public Group save(Group group) {
//        groupRepository.save(group);
        groupRepository.saveAndFlush(group);
        return group;
    }

    @Override
    public void delete(Group group) {
        groupRepository.delete(group);
    }

    @Override
    public Group findByName(String name) {
        return groupRepository.findByName(name);
    }

    @Override
    public Group findById(Long id) {
        return groupRepository.findById(id);
    }

    @Override
    public List<Group> findAll() {
        return Lists.newArrayList(groupRepository.findAll());
    }

    @Override
    public List<Group> findByMembers_User(User user) {
        return groupRepository.findByMembers_User(user);
    }

    @Override
    public List<Group> findByMembers_UserAndRole(User user, MemberRole role) {
        return groupRepository.findByMembers_UserAndMembers_Role(user, role);
    }
}
