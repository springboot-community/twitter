package io.springboot.twitter.configuration;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateModelException;

@Configuration
public class FreemarkerConfiguration {

	@Autowired
	private freemarker.template.Configuration configuration;
	
	@PostConstruct
	public void configuration() throws TemplateModelException {
		this.configuration.setAPIBuiltinEnabled(true);
		DefaultObjectWrapper defaultObjectWrapper = (DefaultObjectWrapper) configuration.getObjectWrapper();
		defaultObjectWrapper.setUseAdaptersForContainers(true);
	}
}