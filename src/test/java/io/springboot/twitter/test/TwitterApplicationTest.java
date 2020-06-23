package io.springboot.twitter.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import io.springboot.twitter.TwitterApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TwitterApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class TwitterApplicationTest {
	
	@Test
	public void test () {
		
	}
}
