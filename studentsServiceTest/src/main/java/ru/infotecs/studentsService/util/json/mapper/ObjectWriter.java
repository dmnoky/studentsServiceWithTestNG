package ru.infotecs.studentsService.util.json.mapper;

import ru.infotecs.studentsService.util.json.annotation.JsonValue;

import java.lang.reflect.Field;
import java.util.Set;

/** Так как задача ограничена JDK 8 и нельзя юзать внешние библиотеки, вместо того же
 * com.fasterxml.jackson.databind.ObjectWriter studentsWriter = new ObjectMapper().writerFor(new TypeReference<List<Student>>(){});
 * пришлось написать это =) Чисто под эту задачу, где не учитывается наследованее, сложные объекты и тд.*/
public class ObjectWriter<T> extends Mapper<T> {
    public ObjectWriter(Class<T> clazz) {
        this.clazz = clazz;
    }

    /** @return "{"id": 1, "name": "std1"}" */
    public StringBuilder mapping(T object) {
        StringBuilder result = new StringBuilder(getClassName()).append('{');
        try {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(JsonValue.class)) {
                    result.append(getField(field, object)).append(',');
                }
            }
            int lstIndex = result.length() - 1;
            if (result.charAt(lstIndex) == ',') result.deleteCharAt(lstIndex);
            result.append('}');
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /** @return "{"students": [{"id": 1,"name": "std1"},{"id": 2,"name": "std2"},{"id": 3,"name": "std3"}]}" */
    public StringBuilder mapping(Set<T> set) {
        StringBuilder result = new StringBuilder("{");
        if (set.size() > 0) {
            String rootNameVal = getArrayName().toString();
            if (!rootNameVal.isEmpty()) result.append(wrapString(rootNameVal)).append(": ");

            result.append('[');

            for (Object o : set) {
                result.append(mapping(clazz.cast(o)).append(','));
            }

            int lstIndex = result.length() - 1;
            if (result.charAt(lstIndex) == ',') result.deleteCharAt(lstIndex);
            result.append(']');
        }
        return result.append('}');
    }
}
