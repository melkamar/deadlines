package com.melkamar.deadlines.services.helpers;

import com.melkamar.deadlines.config.StringConstants;
import com.melkamar.deadlines.dao.task.TaskDAO;
import com.melkamar.deadlines.dao.taskparticipant.TaskParticipantDAO;
import com.melkamar.deadlines.dao.taskparticipant.TaskParticipantDAOHibernate;
import com.melkamar.deadlines.exceptions.AlreadyExistsException;
import com.melkamar.deadlines.model.Group;
import com.melkamar.deadlines.model.TaskParticipant;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.model.task.TaskRole;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     * @return The edited or new TaskParticipant object.
     */
    public TaskParticipant editOrCreateTaskParticipant(User user, Task task, TaskRole role, Group group) {
        TaskParticipant taskParticipant = taskparticipantDAO.findByUserAndTask(user, task);
        if (taskParticipant == null){
            try {
                taskParticipant = createTaskParticipant(user, task, role, group);
            } catch (AlreadyExistsException e) {
                e.printStackTrace();
            }
        } else {
            setSoloOrGroupConnection(taskParticipant, group);
        }

        return taskParticipant;
    }

    /**
     * If group is null, sets solo flag to true.
     * If group is not null, adds the group to the TaskParticipant groups.
     */
    private TaskParticipant setSoloOrGroupConnection(TaskParticipant taskParticipant, Group group){
        if (group == null){
            taskParticipant.setSolo(true);
        } else {
            taskParticipant.addGroup(group);
            group.addParticipant(taskParticipant);
        }

        return taskParticipant;
    }
}
