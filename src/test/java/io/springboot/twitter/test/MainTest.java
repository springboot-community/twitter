package io.springboot.twitter.test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MainTest {
	public static void main(String[] args) throws IOException {
		long val = TimeUnit.DAYS.toMillis(7);
		System.out.println(val / 1000 / 60 / 60 / 24);
	}
}
