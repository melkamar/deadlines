package com.melkamar.deadlines;

import com.melkamar.deadlines.services.helpers.UserHelper;
import com.melkamar.deadlines.model.TaskParticipant;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.DeadlineTask;
import com.melkamar.deadlines.model.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DeadlinesApplication.class)
@WebAppConfiguration
public class DeadlinesApplicationTests {


	@Autowired
	private UserHelper userHelper;

	@Test
	public void contextLoads() {

	}

}
