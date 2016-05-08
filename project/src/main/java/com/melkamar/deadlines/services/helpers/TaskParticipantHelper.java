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

package com.melkamar.deadlines.services.helpers;

import com.melkamar.deadlines.config.StringConstants;
import com.melkamar.deadlines.dao.taskparticipant.TaskParticipantDAO;
import com.melkamar.deadlines.exceptions.AlreadyExistsException;
import com.melkamar.deadlines.exceptions.NotMemberOfException;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.TaskParticipant;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.model.task.TaskRole;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;

/**
 * @author Martin Melka
 */
@Service
public class TaskParticipantHelper {
    @Autowired
    private StringConstants stringConstants;
    @Autowired
    private UrgencyHelper urgencyHelper;
    @Autowired
    private TaskParticipantDAO taskparticipantDAO;

    Logger log = Logger.getLogger(this.getClass());

    public TaskParticipant createTaskParticipant(User user, Task task, TaskRole role, Group group) throws AlreadyExistsException {
        if (taskparticipantDAO.findByUserAndTask(user, task) != null) {
            throw new AlreadyExistsException(MessageFormat.format(stringConstants.EXC_ALREADY_EXISTS_TASK_PARTICIPANT, user, task));
        }

        TaskParticipant taskParticipant = new TaskParticipant(user, task);
        taskParticipant.setRole(role);
        setSoloOrGroupConnection(taskParticipant, group);

        user.addParticipant(taskParticipant);
        task.addParticipant(taskParticipant);

        taskparticipantDAO.save(taskParticipant);
        return taskParticipant;
    }

    /**
     * Creates a new task participation entry.
     * If a TaskParticipant identified by User-Task already exists, it is edited. If it does not exist, it will
     * be created.
     *
     * @param overwriteRole If true, any pre-existing role will be overwritten with the provided role. If false, a role is not set if one already exists.
     * @return The edited or new TaskParticipant object.
     */
    public TaskParticipant editOrCreateTaskParticipant(User user, Task task, TaskRole role, Group group, boolean overwriteRole) {
        TaskParticipant taskParticipant = taskparticipantDAO.findByUserAndTask(user, task);
        if (taskParticipant == null) {
            try {
                taskParticipant = createTaskParticipant(user, task, role, group);
            } catch (AlreadyExistsException e) {
                e.printStackTrace();
            }
        } else {
            setSoloOrGroupConnection(taskParticipant, group);
            if (overwriteRole) taskParticipant.setRole(role);
        }

        return taskParticipant;
    }

    /**
     * Removes a group connection from TaskParticipant.
     * After removal checks if the TaskParticipant should still be associated with the Task or destroyed. (When
     * no other groups are connected and solo is false)
     *
     * @param taskParticipant {@link TaskParticipant} that should be removed from a group.
     * @param group Group from which the {@link TaskParticipant} should be removed.
     */
    @Transactional
    public void removeGroupConnection(TaskParticipant taskParticipant, Group group) {
        taskParticipant.removeGroup(group);
        group.removeTaskParticipant(taskParticipant);

        destroyIfNotRelevant(taskParticipant);
    }

    /**
     * Removes a solo connection from {@link TaskParticipant}.
     * After removal checks if the TaskParticipant should still be associated with the Task or destroyed. (When
     * no other groups are connected and solo is false)
     *
     * @param user User to be removed from a task.
     * @param task Task from which a connection should be removed.
     * @throws NotMemberOfException if the user is not a participant on the task.
     */
    @Transactional
    public void removeSoloConnection(User user, Task task) throws NotMemberOfException {
        TaskParticipant taskParticipant = taskparticipantDAO.findByUserAndTask(user, task);

        if (taskParticipant == null) {
            throw new NotMemberOfException(MessageFormat.format(stringConstants.EXC_USER_NOT_PARTICIPANT, user, task));
        }

        taskParticipant.setSolo(false);

        destroyIfNotRelevant(taskParticipant);
    }

    @Transactional
    public TaskParticipant getTaskParticipant(User user, Task task) {
        return taskparticipantDAO.findByUserAndTask(user, task);
    }

    /**
     * If the TaskParticipant is not relevant (not solo or connected by a group), method will destroy it.
     *
     * @param taskParticipant {@link TaskParticipant} object that should be destroyed if it is not needed anymore.
     */
    private void destroyIfNotRelevant(TaskParticipant taskParticipant) {
        if (!shouldBeDestroyed(taskParticipant)) return;

        taskParticipant.getTask().removeParticipant(taskParticipant);
        taskParticipant.getUser().removeParticipant(taskParticipant);
        // Because it should be destroyed, it already contains no references in Group
        taskparticipantDAO.delete(taskParticipant);
    }

    /**
     * Checks if the TaskParticipant is still relevant (via solo or group connection to a Task).
     *
     * @return True if the TaskParticipant should be removed.
     */
    private boolean shouldBeDestroyed(TaskParticipant taskParticipant) {
        return !taskParticipant.getSolo() && taskParticipant.getGroups().size() == 0;
    }

    /**
     * If group is null, sets solo flag to true.
     * If group is not null, adds the group to the TaskParticipant groups.
     */
    private TaskParticipant setSoloOrGroupConnection(TaskParticipant taskParticipant, Group group) {
        if (group == null) {
            taskParticipant.setSolo(true);
        } else {
            taskParticipant.addGroup(group);
            group.addParticipant(taskParticipant);
        }

        return taskParticipant;
    }
}
