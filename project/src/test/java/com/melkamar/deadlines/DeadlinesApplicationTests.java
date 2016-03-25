package com.melkamar.deadlines;

import com.melkamar.deadlines.model.TaskParticipant;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.model.task.DeadlineTask;
import com.melkamar.deadlines.model.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DeadlinesApplication.class)
@WebAppConfiguration
public class DeadlinesApplicationTests {

	@Test
	public void contextLoads() {

	}

	@Test
	public void createParticipant(){
		User user = new User();
		user.setName("User1");

		Task task = new DeadlineTask();

		TaskParticipant participant = TaskParticipant.createTaskParticipant(user, task);
	}

}
