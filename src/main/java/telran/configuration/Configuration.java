package telran.configuration;

import java.util.*;

import telran.configuration.annotations.Value;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Configuration {
	private static final String DEFAULT_CONFIG_FILE = "application.properties";
	Object configObj;
	Properties properties;

	public Configuration(Object configObj, String configFile) {
		this.configObj = configObj;

		this.properties = new Properties();
		
		try (FileInputStream fis = new FileInputStream(configFile)) {
			properties.load(fis);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}

	public Configuration(Object configObject) throws Exception {
		this(configObject, DEFAULT_CONFIG_FILE);
	}

	public void configInjection() {
		Arrays.stream(configObj.getClass().getDeclaredFields()).filter(f -> f.isAnnotationPresent(Value.class))
				.forEach(this::injection);
	}

	void injection(Field field) {
		Value valueAnnotation = field.getAnnotation(Value.class);
		Object value = getValue(valueAnnotation.value(), field.getType().getSimpleName().toLowerCase());
		setValue(field, value);
	}

	private Object getValue(String value, String typeName) {
		try {
			String[] tokens = value.split(":");
			String propertyValue = properties.getProperty(tokens[0]);

			Method method = getClass().getDeclaredMethod(typeName + "Convertion", String.class);
			return method.invoke(this, propertyValue == null ? tokens[1] : propertyValue);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	void setValue(Field field, Object value) {
		try {
			field.setAccessible(true);
			field.set(configObj, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// assumption: supported data types: int, long, float, double, String
	Integer intConvertion(String value) {
		return Integer.valueOf(value);
	}

	Long longConvertion(String value) {
		return Long.valueOf(value);
	}

	Float floatConvertion(String value) {
		return Float.valueOf(value);
	}

	Double doubleConvertion(String value) {
		return Double.valueOf(value);
	}

	String stringConvertion(String value) {
		return value;
	}

}
