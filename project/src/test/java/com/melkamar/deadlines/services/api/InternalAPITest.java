package com.melkamar.deadlines.services.api;

import com.melkamar.deadlines.dao.task.TaskDAO;
import com.melkamar.deadlines.model.task.DeadlineTask;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.model.task.TaskStatus;
import com.melkamar.deadlines.model.task.Urgency;
import com.melkamar.deadlines.services.helpers.UrgencyHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.eq;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 07.04.2016 20:03
 */
@RunWith(MockitoJUnitRunner.class)
public class InternalAPITest {
    @Mock
    TaskDAO taskDAO;
    @Mock
    UrgencyHelper urgencyHelper;

    @InjectMocks
    InternalAPI internalAPI;

    List<Task> tasksOpen = new ArrayList<>();
    List<Task> tasksInProgress = new ArrayList<>();
    List<Task> tasksCompleted = new ArrayList<>();
    List<Task> tasksCancelled = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        tasksOpen.add(new DeadlineTask(new Date(), new Date(), new Urgency()).setStatus(TaskStatus.OPEN).setName("Task 1A"));
        tasksOpen.add(new DeadlineTask(new Date(), new Date(), new Urgency()).setStatus(TaskStatus.OPEN).setName("Task 1B"));
        tasksInProgress.add(new DeadlineTask(new Date(), new Date(), new Urgency()).setStatus(TaskStatus.IN_PROGRESS).setName("Task 2A"));
        tasksInProgress.add(new DeadlineTask(new Date(), new Date(), new Urgency()).setStatus(TaskStatus.IN_PROGRESS).setName("Task 2B"));
        tasksCompleted.add(new DeadlineTask(new Date(), new Date(), new Urgency()).setStatus(TaskStatus.COMPLETED).setName("Task 3A"));
        tasksCancelled.add(new DeadlineTask(new Date(), new Date(), new Urgency()).setStatus(TaskStatus.CANCELLED).setName("Task 4A"));

        Mockito.when(taskDAO.findByStatus(TaskStatus.OPEN)).thenReturn(tasksOpen);
        Mockito.when(taskDAO.findByStatus(TaskStatus.IN_PROGRESS)).thenReturn(tasksInProgress);
        Mockito.when(taskDAO.findByStatus(TaskStatus.COMPLETED)).thenReturn(tasksCompleted);
        Mockito.when(taskDAO.findByStatus(TaskStatus.CANCELLED)).thenReturn(tasksCancelled);
    }

    /**
     * Check that {@link InternalAPI#updateAllUrgencies()} is called on appropriate Tasks correctly.
     * @throws Exception
     */
    @Test
    public void updateAllUrgencies() throws Exception {
        internalAPI.updateAllUrgencies();

        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.verify(urgencyHelper, Mockito.times(4)).updateUrgency(taskArgumentCaptor.capture(), eq(false));

        List<Task> activeTasks = new ArrayList<>();
        activeTasks.addAll(tasksOpen);
        activeTasks.addAll(tasksInProgress);

        Assert.assertTrue(activeTasks.contains(taskArgumentCaptor.getAllValues().get(0)));
        Assert.assertTrue(activeTasks.contains(taskArgumentCaptor.getAllValues().get(1)));
        Assert.assertTrue(activeTasks.contains(taskArgumentCaptor.getAllValues().get(2)));
        Assert.assertTrue(activeTasks.contains(taskArgumentCaptor.getAllValues().get(3)));
    }
}