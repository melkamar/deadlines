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
 * Created by Martin Melka (martin.melka@gmail.com)
 * 27.03.2016 15:44
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
     * @param taskParticipant
     * @param group
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
     */
    @Transactional
    public void removeSoloConnection(User user, Task task) throws NotMemberOfException {
        TaskParticipant taskParticipant = taskparticipantDAO.findByUserAndTask(user, task);

        if (taskParticipant == null){
            throw new NotMemberOfException(MessageFormat.format(stringConstants.EXC_USER_NOT_PARTICIPANT, user, task));
        }

        taskParticipant.setSolo(false);

        destroyIfNotRelevant(taskParticipant);
    }

    /**
     * If the TaskParticipant is not relevant (not solo or connected by a group), method will destroy it.
     *
     * @param taskParticipant
     * @return
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
