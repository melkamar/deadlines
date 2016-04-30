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

package com.melkamar.deadlines.services.api.implementation;

import com.melkamar.deadlines.dao.task.TaskDAO;
import com.melkamar.deadlines.model.task.Task;
import com.melkamar.deadlines.model.task.TaskStatus;
import com.melkamar.deadlines.services.api.InternalApi;
import com.melkamar.deadlines.services.helpers.UrgencyHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Martin Melka
 */
@Service("internalApi")
@Transactional
public class InternalApiImpl implements InternalApi {
    @Autowired
    private TaskDAO taskDAO;
    @Autowired
    private UrgencyHelper urgencyHelper;


    @Override
    public void updateAllUrgencies(){
        updateAllUrgencies(false);
    }

    @Override
    public void updateAllUrgencies(boolean force){
        for (TaskStatus status: Task.activeStates){
            for (Task task: taskDAO.findByStatus(status)){
                urgencyHelper.updateUrgency(task, force);
            }
        }
    }
}
