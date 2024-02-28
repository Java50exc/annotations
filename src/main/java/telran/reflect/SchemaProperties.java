package telran.reflect;

import java.lang.reflect.Field;
import java.util.Arrays;

public class SchemaProperties {

	public static void displayFieldProperties(Object obj) {
		Field[] fields = obj.getClass().getDeclaredFields();
		Field[] idFields = Arrays.stream(fields).filter(e -> e.isAnnotationPresent(Id.class)).toArray(Field[]::new);
		Field[] indexFields = Arrays.stream(fields).filter(e -> e.isAnnotationPresent(Index.class))
				.toArray(Field[]::new);

		if (idFields.length != 1) {
			throw new IllegalStateException(idFields.length > 1 ? "Field Id must be one" : "No field Id found");
		}

		Arrays.stream(idFields).forEach(System.out::println);
		Arrays.stream(indexFields).forEach(System.out::println);
	}

}
