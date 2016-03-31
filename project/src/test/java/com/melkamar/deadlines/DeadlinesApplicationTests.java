package com.melkamar.deadlines;

import com.melkamar.deadlines.services.api.UserAPI;
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
	private UserAPI userAPI;

	@Test
	public void contextLoads() {

	}

}
