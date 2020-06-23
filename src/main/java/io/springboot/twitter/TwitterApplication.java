package io.springboot.twitter;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.context.ApplicationContext;
/**
 * 
 *  Twitter Application
 *
 */
@SpringBootApplication(exclude = { JacksonAutoConfiguration.class })
// @EnableJpaRepositories(basePackages = { "io.springboot.twitter.repository"} )
// @EntityScan(basePackages = { "io.springboot.twitter.domain.entity" })
public class TwitterApplication {
	
	public static ApplicationContext applicationContext = null;
	
	public static void main (String[] args) {
		
		String configLocation = String.join(File.separator, "file:${user.home}", "twitter", "config", "");
		System.setProperty(ConfigFileApplicationListener.CONFIG_ADDITIONAL_LOCATION_PROPERTY, configLocation);
		
		SpringApplication springApplication = new SpringApplication(TwitterApplication.class);
		springApplication.addListeners(new ApplicationPidFileWriter());
		applicationContext = springApplication.run(args);
	}
}
