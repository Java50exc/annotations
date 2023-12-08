package telran.configuration;

import java.util.*;

import telran.configuration.annotations.Value;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
public class Configuration {
	private static final String DEFAULT_CONFIG_FILE = "application.properties";
	Object configObj;
	Properties properties = new Properties();
	//TODO for HW #51
	public Configuration(Object configObj, String configFile) throws Exception{
		this.configObj = configObj;
		properties.load(new FileInputStream(configFile));
	//	String value = properties.getProperty("<property name>", "<defaultValue>");
		//<property name>=<value>
	}
	public Configuration(Object configObject) throws Exception {
		this(configObject, DEFAULT_CONFIG_FILE);
	}
	public void configInjection() {
		Arrays.stream(configObj.getClass().getDeclaredFields())
		.filter(f -> f.isAnnotationPresent(Value.class))
		.forEach(this::injection);
	}
	void injection(Field field) {
		Value valueAnnotation = field.getAnnotation(Value.class);
		
	
		
		Object value = getValue(valueAnnotation.value(), field.getType().getSimpleName().toLowerCase());
		setValue(field, value);
		
	}
	private Object getValue(String value, String typeName) {
		
		String [] tokens = value.split(":");
		String propertyName = tokens[0];
		String defaultValue = tokens.length == 2 ? tokens[1] : "";
		//value structure: <property name>:<default value>
		String valueFile = properties.getProperty(propertyName, defaultValue);
		if (valueFile.isEmpty()) {
			throw new RuntimeException("no value for property " + propertyName);
		}
		
		try {
			Method method = getClass().getDeclaredMethod(typeName + "Convertion", String.class);
			return method.invoke(this, valueFile); //FIXME for HW #51
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
	//assumption: supported data types: int, long, float, double, String
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
