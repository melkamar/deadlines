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

package com.melkamar.deadlines.dao.taskparticipant;

import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.TaskParticipant;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author Martin Melka
 */
@Service("taskparticipantDAO")
public class TaskParticipantDAOHibernate implements TaskParticipantDAO {
    @Autowired
    private TaskParticipantRepository taskParticipantRepository;

    @Override
    public long count() {
        return taskParticipantRepository.count();
    }

    @Override
    public TaskParticipant save(TaskParticipant taskParticipant) {
        return taskParticipantRepository.save(taskParticipant);
    }

    @Override
    public void delete(TaskParticipant taskParticipant) {
        taskParticipantRepository.delete(taskParticipant);
    }

    @Override
    public TaskParticipant findByUserAndTask(User user, Task task) {
        return taskParticipantRepository.findByUserAndTask(user, task);
    }

    @Override
    public Set<TaskParticipant> findByUserAndGroups(User user, Group group) {
        return taskParticipantRepository.findByUserAndGroups(user, group);
    }

    @Override
    public Set<TaskParticipant> findByTaskAndGroups(Task task, Group group) {
        return taskParticipantRepository.findByTaskAndGroups(task, group);
    }

    @Override
    public Set<TaskParticipant> findByUser(User user) {
        return taskParticipantRepository.findByUser(user);
    }
}
