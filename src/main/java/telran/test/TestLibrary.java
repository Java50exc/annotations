package telran.test;

import java.lang.reflect.Method;
import java.util.Arrays;

import telran.reflect.BeforeEach;
import telran.reflect.Test;

public class TestLibrary {
	public static void launchTest(Object testObj) throws Exception {
		Method[] testMethods = testObj.getClass().getDeclaredMethods();
		var beforeEachMethods = Arrays.stream(testMethods).filter(m -> m.isAnnotationPresent(BeforeEach.class))
				.map(e -> {
					e.setAccessible(true);
					return e;
				}).toArray(Method[]::new);

		for (Method method : testMethods) {
			if (method.isAnnotationPresent(Test.class)) {
				runMethods(beforeEachMethods, testObj);
				method.setAccessible(true);
				method.invoke(testObj);
			}
		}
	}

	public static void runMethods(Method[] methods, Object obj) {
		for (var method : methods) {
			try {
				method.invoke(obj);
			} catch (Exception e) {} 
		}
	}
}
