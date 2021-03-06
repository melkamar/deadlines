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

package com.melkamar.deadlines.dao.task;

import com.google.common.collect.Lists;
import com.melkamar.deadlines.config.StringConstants;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.DeadlineTask;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.model.task.TaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * @author Martin Melka
 */
@Service("taskDAO")
public class TaskDAOHibernate implements TaskDAO {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private StringConstants stringConstants;


    @Override
    public long count() {
        return taskRepository.count();
    }

    @Override
    public Task save(Task task) {
        taskRepository.saveAndFlush(task);
        return task;
    }

    @Override
    public Task findById(Long id) {
        return taskRepository.findById(id);
    }

    @Override
    public List<Task> findAll() {
        return Lists.newArrayList(taskRepository.findAll());
    }

    @Override
    public List<Task> findByUser(User user) {
        return taskRepository.findByParticipants_User(user);
    }

    @Override
    public List<Task> findByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    @Override
    public List<Task> findByUserOrderByNameAsc(User user) {
        return taskRepository.findByParticipants_UserOrderByNameAsc(user);
    }

    @Override
    public List<Task> findByUserOrderByNameDesc(User user) {
        return taskRepository.findByParticipants_UserOrderByNameDesc(user);
    }

    @Override
    public List<Task> findByUserOrderByDateCreatedAsc(User user) {
        return taskRepository.findByParticipants_UserOrderByDateCreatedAsc(user);
    }

    @Override
    public List<Task> findByUserOrderByDateCreatedDesc(User user) {
        return taskRepository.findByParticipants_UserOrderByDateCreatedDesc(user);
    }

    @Override
    public List<Task> findByUserOrderByPriorityAsc(User user) {
        return taskRepository.findByParticipants_UserOrderByPriorityAsc(user);
    }

    @Override
    public List<Task> findByUserOrderByPriorityDesc(User user) {
        return taskRepository.findByParticipants_UserOrderByPriorityDesc(user);
    }

    @Override
    public List<Task> findByUserOrderByUrgency_ValueAsc(User user) {
        return taskRepository.findByParticipants_UserOrderByUrgency_ValueAsc(user);
    }

    @Override
    public List<Task> findByUserOrderByUrgency_ValueDesc(User user) {
        return taskRepository.findByParticipants_UserOrderByUrgency_ValueDesc(user);
    }

    @Override
    public List<Task> findByUserOrderByDeadlineAsc(User user) {
        List<Task> tasks = taskRepository.findByParticipants_User(user);
        tasks.sort(new DeadlineTaskComparator(true));
        return tasks;
    }

    @Override
    public List<Task> findByUserOrderByDeadlineDesc(User user) {
        List<Task> tasks = taskRepository.findByParticipants_User(user);
        tasks.sort(new DeadlineTaskComparator(false));
        return tasks;
    }

    @Override
    public List<Task> findByUserOrderByWorkedAsc(User user) {
        List<Task> tasks = taskRepository.findByParticipants_User(user);
        tasks.sort(new WorkedPercentComparator(true));
        return tasks;
    }

    @Override
    public List<Task> findByUserOrderByWorkedDesc(User user) {
        List<Task> tasks = taskRepository.findByParticipants_User(user);
        tasks.sort(new WorkedPercentComparator(false));
        return tasks;
    }


    private class DeadlineTaskComparator implements Comparator<Task> {
        private final int ascending;

        public DeadlineTaskComparator(boolean ascending) {
            this.ascending = (ascending ? 1 : -1);
        }

        @Override
        public int compare(Task o1, Task o2) {
            boolean o1deadlineTask = o1 instanceof DeadlineTask;
            boolean o2deadlineTask = o2 instanceof DeadlineTask;
            if (o1deadlineTask && !o2deadlineTask) return -1 * ascending;
            if (!o1deadlineTask && o2deadlineTask) return ascending;
            if (!o1deadlineTask && !o2deadlineTask) return 0;

            DeadlineTask d1 = (DeadlineTask) o1;
            DeadlineTask d2 = (DeadlineTask) o2;

            return d1.getDeadline().compareTo(d2.getDeadline()) * ascending;
        }
    }

    private class WorkedPercentComparator implements Comparator<Task> {
        private final int ascending;

        public WorkedPercentComparator(boolean ascending) {
            this.ascending = (ascending ? 1 : -1);
        }

        @Override
        public int compare(Task o1, Task o2) {
            Double percentage1 = o1.getWorkedPercentage();
            Double percentage2 = o2.getWorkedPercentage();

            return percentage1.compareTo(percentage2) * ascending;
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public List<Task> findByGroup(Group group) {
        return taskRepository.findBySharedGroups(group);
    }

    @Override
    public List<Task> findByGroupOrderByNameAsc(Group group) {
        return taskRepository.findBySharedGroupsOrderByNameAsc(group);
    }

    @Override
    public List<Task> findByGroupOrderByNameDesc(Group group) {
        return taskRepository.findBySharedGroupsOrderByNameDesc(group);
    }

    @Override
    public List<Task> findByGroupOrderByDateCreatedAsc(Group group) {
        return taskRepository.findBySharedGroupsOrderByDateCreatedAsc(group);
    }

    @Override
    public List<Task> findByGroupOrderByDateCreatedDesc(Group group) {
        return taskRepository.findBySharedGroupsOrderByDateCreatedDesc(group);
    }

    @Override
    public List<Task> findByGroupOrderByPriorityAsc(Group group) {
        return taskRepository.findBySharedGroupsOrderByPriorityAsc(group);
    }

    @Override
    public List<Task> findByGroupOrderByPriorityDesc(Group group) {
        return taskRepository.findBySharedGroupsOrderByPriorityDesc(group);
    }

    @Override
    public List<Task> findByGroupOrderByUrgency_ValueAsc(Group group) {
        return taskRepository.findBySharedGroupsOrderByUrgency_ValueAsc(group);
    }

    @Override
    public List<Task> findByGroupOrderByUrgency_ValueDesc(Group group) {
        return taskRepository.findBySharedGroupsOrderByUrgency_ValueDesc(group);
    }

    @Override
    public List<Task> findByGroupOrderByDeadlineAsc(Group group) {
        List<Task> tasks = taskRepository.findBySharedGroups(group);
        tasks.sort(new DeadlineTaskComparator(true));
        return tasks;
    }

    @Override
    public List<Task> findByGroupOrderByDeadlineDesc(Group group) {
        List<Task> tasks = taskRepository.findBySharedGroups(group);
        tasks.sort(new DeadlineTaskComparator(false));
        return tasks;
    }

    @Override
    public List<Task> findByGroupOrderByWorkedAsc(Group group) {
        List<Task> tasks = taskRepository.findBySharedGroups(group);
        tasks.sort(new WorkedPercentComparator(true));
        return tasks;
    }

    @Override
    public List<Task> findByGroupOrderByWorkedDesc(Group group) {
        List<Task> tasks = taskRepository.findBySharedGroups(group);
        tasks.sort(new WorkedPercentComparator(false));
        return tasks;
    }

}