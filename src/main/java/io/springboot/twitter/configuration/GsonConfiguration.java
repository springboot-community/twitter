package io.springboot.twitter.configuration;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.boot.autoconfigure.gson.GsonBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;


@Configuration("commonGsonConfiguration")
public class GsonConfiguration {
	
	static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
	
	@Bean
	public GsonBuilder gsonBuilder(List<GsonBuilderCustomizer> customizers) {

		GsonBuilder builder = new GsonBuilder();
		customizers.forEach((c) -> c.customize(builder));
		

		/**
		 * 日期类型的格式化
		 */
		builder.registerTypeHierarchyAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
			@Override
			public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
				return new JsonPrimitive(DATE_TIME_FORMATTER.format(src));
			}
		});
		builder.registerTypeHierarchyAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
			@Override
			public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
				return new JsonPrimitive(DATE_FORMATTER.format(src));
			}
		});
		builder.registerTypeHierarchyAdapter(LocalTime.class, new JsonSerializer<LocalTime>() {
			@Override
			public JsonElement serialize(LocalTime src, Type typeOfSrc, JsonSerializationContext context) {
				return new JsonPrimitive(TIME_FORMATTER.format(src));
			}
		});
		return builder;
	}
}
