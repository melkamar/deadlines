package com.melkamar.deadlines.services.helpers.urgency;

import com.melkamar.deadlines.model.task.DeadlineTask;
import com.melkamar.deadlines.services.DateConvertor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 06.04.2016 20:46
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultUrgencyComputerTest {
    UrgencyComputer urgencyComputer = new DefaultUrgencyComputer();

    @Mock
    DeadlineTask task;

    /**
     * Same work estimate, different deadlines.
     */
    @Test
    public void computeDeadlineTaskUrgency1() throws Exception {
        Mockito.when(task.getWorkEstimate()).thenReturn(2d);

        Mockito.when(task.getDeadline()).thenReturn(DateConvertor.localDateTimeToDate(LocalDateTime.now().plusHours(10)));
        double urgency1 = urgencyComputer.computeDeadlineTaskUrgency(task);

        Mockito.when(task.getDeadline()).thenReturn(DateConvertor.localDateTimeToDate(LocalDateTime.now().plusHours(8)));
        double urgency2 = urgencyComputer.computeDeadlineTaskUrgency(task);

        assertTrue(urgency1 < urgency2);
    }

    /**
     * Different work estimate, same deadlines.
     */
    @Test
    public void computeDeadlineTaskUrgency2() throws Exception {
        Mockito.when(task.getDeadline()).thenReturn(DateConvertor.localDateTimeToDate(LocalDateTime.now().plusHours(8)));

        Mockito.when(task.getWorkEstimate()).thenReturn(1d);
        double urgency1 = urgencyComputer.computeDeadlineTaskUrgency(task);

        Mockito.when(task.getWorkEstimate()).thenReturn(2d);
        double urgency2 = urgencyComputer.computeDeadlineTaskUrgency(task);

        assertTrue(urgency1 < urgency2);
    }

    /**
     * Different work estimate, same deadlines. Test short deadlines and too much work.
     */
    @Test
    public void computeDeadlineTaskUrgency3() throws Exception {
        Mockito.when(task.getDeadline()).thenReturn(DateConvertor.localDateTimeToDate(LocalDateTime.now().plusHours(8)));

        Double[] estimates = new Double[]{6d, 7d, 8d, 9d, 10d, 20d, 40d};
        for (int i=0; i<estimates.length-2; i++){
            Mockito.when(task.getWorkEstimate()).thenReturn(estimates[i]);
            double urgency1 = urgencyComputer.computeDeadlineTaskUrgency(task);

            Mockito.when(task.getWorkEstimate()).thenReturn(estimates[i+1]);
            double urgency2 = urgencyComputer.computeDeadlineTaskUrgency(task);

            Assert.assertTrue(urgency1<urgency2);
        }
    }

    /**
     * Progress reporting on a task. When there is more work done than estimated, expect 0 urgency.
     */
    @Test
    public void computeDeadlineTaskUrgency4() throws Exception {
        Mockito.when(task.getDeadline()).thenReturn(DateConvertor.localDateTimeToDate(LocalDateTime.now().plusHours(10)));
        Mockito.when(task.getWorkEstimate()).thenReturn(15d);

        Mockito.when(task.getManhoursWorked()).thenReturn(0d);
        double urgency1 = urgencyComputer.computeDeadlineTaskUrgency(task);

        Mockito.when(task.getManhoursWorked()).thenReturn(1d);
        double urgency2 = urgencyComputer.computeDeadlineTaskUrgency(task);

        Mockito.when(task.getManhoursWorked()).thenReturn(3d);
        double urgency3 = urgencyComputer.computeDeadlineTaskUrgency(task);

        Mockito.when(task.getManhoursWorked()).thenReturn(9d);
        double urgency4 = urgencyComputer.computeDeadlineTaskUrgency(task);

        Mockito.when(task.getManhoursWorked()).thenReturn(10d);
        double urgency5 = urgencyComputer.computeDeadlineTaskUrgency(task);

        Mockito.when(task.getManhoursWorked()).thenReturn(11d);
        double urgency6 = urgencyComputer.computeDeadlineTaskUrgency(task);

        Mockito.when(task.getManhoursWorked()).thenReturn(15d);
        double urgency7 = urgencyComputer.computeDeadlineTaskUrgency(task);

        Mockito.when(task.getManhoursWorked()).thenReturn(16d);
        double urgency8 = urgencyComputer.computeDeadlineTaskUrgency(task);

        Mockito.when(task.getManhoursWorked()).thenReturn(20d);
        double urgency9 = urgencyComputer.computeDeadlineTaskUrgency(task);

        assertTrue(urgency1 > urgency2);
        assertTrue(urgency2 > urgency3);
        assertTrue(urgency3 > urgency4);
        assertTrue(urgency4 > urgency5);
        assertTrue(urgency5 > urgency6);
        assertTrue(urgency6 > urgency7);
        Assert.assertEquals(urgency7, urgency8, 0.001);
        Assert.assertEquals(urgency8, urgency9, 0.001);
    }

    @Test
    public void computeGrowingTaskUrgency() throws Exception {

    }
}