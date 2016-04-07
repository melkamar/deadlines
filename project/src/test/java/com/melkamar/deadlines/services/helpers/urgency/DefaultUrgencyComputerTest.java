package com.melkamar.deadlines.services.helpers.urgency;

import com.melkamar.deadlines.model.task.DeadlineTask;
import com.melkamar.deadlines.model.task.GrowingTask;
import com.melkamar.deadlines.model.task.Urgency;
import com.melkamar.deadlines.services.DateConvertor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 06.04.2016 20:46
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultUrgencyComputerTest {
    UrgencyComputer urgencyComputer = new DefaultUrgencyComputer();

    @Mock
    DeadlineTask deadlineTask;

    @Mock
    GrowingTask growingTask;

    /**
     * Same work estimate, different deadlines.
     */
    @Test
    public void computeDeadlineTaskUrgency1() throws Exception {
        Mockito.when(deadlineTask.getWorkEstimate()).thenReturn(2d);

        Mockito.when(deadlineTask.getDeadline()).thenReturn(DateConvertor.localDateTimeToDate(LocalDateTime.now().plusHours(10)));
        double urgency1 = urgencyComputer.computeDeadlineTaskUrgency(deadlineTask);

        Mockito.when(deadlineTask.getDeadline()).thenReturn(DateConvertor.localDateTimeToDate(LocalDateTime.now().plusHours(8)));
        double urgency2 = urgencyComputer.computeDeadlineTaskUrgency(deadlineTask);

        assertTrue(urgency1 < urgency2);
    }

    /**
     * Different work estimate, same deadlines.
     */
    @Test
    public void computeDeadlineTaskUrgency2() throws Exception {
        Mockito.when(deadlineTask.getDeadline()).thenReturn(DateConvertor.localDateTimeToDate(LocalDateTime.now().plusHours(8)));

        Mockito.when(deadlineTask.getWorkEstimate()).thenReturn(1d);
        double urgency1 = urgencyComputer.computeDeadlineTaskUrgency(deadlineTask);

        Mockito.when(deadlineTask.getWorkEstimate()).thenReturn(2d);
        double urgency2 = urgencyComputer.computeDeadlineTaskUrgency(deadlineTask);

        assertTrue(urgency1 < urgency2);
    }

    /**
     * Different work estimate, same deadlines. Test short deadlines and too much work.
     */
    @Test
    public void computeDeadlineTaskUrgency3() throws Exception {
        Mockito.when(deadlineTask.getDeadline()).thenReturn(DateConvertor.localDateTimeToDate(LocalDateTime.now().plusHours(8)));

        Double[] estimates = new Double[]{6d, 7d, 8d, 9d, 10d, 20d, 40d};
        for (int i = 0; i < estimates.length - 2; i++) {
            Mockito.when(deadlineTask.getWorkEstimate()).thenReturn(estimates[i]);
            double urgency1 = urgencyComputer.computeDeadlineTaskUrgency(deadlineTask);

            Mockito.when(deadlineTask.getWorkEstimate()).thenReturn(estimates[i + 1]);
            double urgency2 = urgencyComputer.computeDeadlineTaskUrgency(deadlineTask);

            Assert.assertTrue(urgency1 < urgency2);
        }
    }

    /**
     * Progress reporting on a deadlineTask. When there is more work done than estimated, expect 0 urgency.
     */
    @Test
    public void computeDeadlineTaskUrgency4() throws Exception {
        Mockito.when(deadlineTask.getDeadline()).thenReturn(DateConvertor.localDateTimeToDate(LocalDateTime.now().plusHours(10)));
        Mockito.when(deadlineTask.getWorkEstimate()).thenReturn(15d);

        Mockito.when(deadlineTask.getManhoursWorked()).thenReturn(0d);
        double urgency1 = urgencyComputer.computeDeadlineTaskUrgency(deadlineTask);

        Mockito.when(deadlineTask.getManhoursWorked()).thenReturn(1d);
        double urgency2 = urgencyComputer.computeDeadlineTaskUrgency(deadlineTask);

        Mockito.when(deadlineTask.getManhoursWorked()).thenReturn(3d);
        double urgency3 = urgencyComputer.computeDeadlineTaskUrgency(deadlineTask);

        Mockito.when(deadlineTask.getManhoursWorked()).thenReturn(9d);
        double urgency4 = urgencyComputer.computeDeadlineTaskUrgency(deadlineTask);

        Mockito.when(deadlineTask.getManhoursWorked()).thenReturn(10d);
        double urgency5 = urgencyComputer.computeDeadlineTaskUrgency(deadlineTask);

        Mockito.when(deadlineTask.getManhoursWorked()).thenReturn(11d);
        double urgency6 = urgencyComputer.computeDeadlineTaskUrgency(deadlineTask);

        Mockito.when(deadlineTask.getManhoursWorked()).thenReturn(15d);
        double urgency7 = urgencyComputer.computeDeadlineTaskUrgency(deadlineTask);

        Mockito.when(deadlineTask.getManhoursWorked()).thenReturn(16d);
        double urgency8 = urgencyComputer.computeDeadlineTaskUrgency(deadlineTask);

        Mockito.when(deadlineTask.getManhoursWorked()).thenReturn(20d);
        double urgency9 = urgencyComputer.computeDeadlineTaskUrgency(deadlineTask);

        assertTrue(urgency1 > urgency2);
        assertTrue(urgency2 > urgency3);
        assertTrue(urgency3 > urgency4);
        assertTrue(urgency4 > urgency5);
        assertTrue(urgency5 > urgency6);
        assertTrue(urgency6 > urgency7);
        Assert.assertEquals(urgency7, urgency8, 0.001);
        Assert.assertEquals(urgency8, urgency9, 0.001);
    }

    @Mock
    Urgency mockUrgency;

    @Test
    public void computeGrowingTaskUrgency() throws Exception {
        Mockito.when(growingTask.getGrowspeed()).thenReturn(10d);
        Mockito.when(growingTask.getUrgency()).thenReturn(mockUrgency);

        double startUrgency = 0;
        Mockito.when(growingTask.getUrgency().getValue()).thenReturn(startUrgency);

        Mockito.when(mockUrgency.getLastUpdate()).thenReturn(DateConvertor.localDateTimeToDate(LocalDateTime.now().minusHours(10)));
        double newUrgency1 = urgencyComputer.computeGrowingTaskUrgency(growingTask);

        Mockito.when(mockUrgency.getLastUpdate()).thenReturn(DateConvertor.localDateTimeToDate(LocalDateTime.now().minusHours(11)));
        double newUrgency2 = urgencyComputer.computeGrowingTaskUrgency(growingTask);

        Mockito.when(mockUrgency.getLastUpdate()).thenReturn(DateConvertor.localDateTimeToDate(LocalDateTime.now().minusHours(12)));
        double newUrgency3 = urgencyComputer.computeGrowingTaskUrgency(growingTask);

        // Check that the longer it has been since the last update, the bigger the urgency is
        Assert.assertTrue(newUrgency1 > startUrgency);
        Assert.assertTrue(newUrgency2 > newUrgency1);
        Assert.assertTrue(newUrgency3 > newUrgency2);
    }

    /**
     * Check that there is a max value to the Urgency.
     */
    @Test
    public void computeGrowingTaskUrgency_maxUrgency(){
        Mockito.when(growingTask.getGrowspeed()).thenReturn(10000000d);
        Mockito.when(growingTask.getUrgency()).thenReturn(mockUrgency);

        double startUrgency = 0;
        Mockito.when(growingTask.getUrgency().getValue()).thenReturn(startUrgency);

        Mockito.when(mockUrgency.getLastUpdate()).thenReturn(DateConvertor.localDateTimeToDate(LocalDateTime.now().minusHours(100000)));
        double newUrgency1 = urgencyComputer.computeGrowingTaskUrgency(growingTask);
        Mockito.when(mockUrgency.getLastUpdate()).thenReturn(DateConvertor.localDateTimeToDate(LocalDateTime.now().minusHours(100001)));
        double newUrgency2 = urgencyComputer.computeGrowingTaskUrgency(growingTask);

        Assert.assertEquals(newUrgency1, newUrgency2, 0.00001);
    }

    @Test
    public void computeGrowingTaskUrgency_zeroGrow(){
        Mockito.when(growingTask.getGrowspeed()).thenReturn(0d);
        Mockito.when(growingTask.getUrgency()).thenReturn(mockUrgency);

        double startUrgency = 0;
        Mockito.when(growingTask.getUrgency().getValue()).thenReturn(startUrgency);

        Mockito.when(mockUrgency.getLastUpdate()).thenReturn(DateConvertor.localDateTimeToDate(LocalDateTime.now().minusHours(10)));
        double newUrgency1 = urgencyComputer.computeGrowingTaskUrgency(growingTask);
        Mockito.when(mockUrgency.getLastUpdate()).thenReturn(DateConvertor.localDateTimeToDate(LocalDateTime.now().minusHours(11)));
        double newUrgency2 = urgencyComputer.computeGrowingTaskUrgency(growingTask);

        Assert.assertEquals(newUrgency1, newUrgency2, 0.00001);
        Assert.assertEquals(newUrgency1, 0, 0.00001);
    }
}