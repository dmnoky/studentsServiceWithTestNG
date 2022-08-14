package ru.infotecs.studentsService.util.json.mapper;

import ru.infotecs.studentsService.util.json.annotation.JsonValue;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Так как задача ограничена JDK 8 и нельзя юзать внешние библиотеки, вместо того же
 * com.fasterxml.jackson.databind.ObjectReader studentsWriter = new ObjectMapper().readerFor(new TypeReference<List<Student>>(){});
 * пришлось написать это =) Чисто под эту задачу, где не учитывается наследованее, сложные объекты и тд.*/
public class ObjectReader<T> extends Mapper<T> {
    public static final String
            VALUE_RGX_FORMAT = "%s\\s*:\\s*\"?([^,\"]*)\"?\\s*,?",
            ARRAY_NAME_RGX_FORMAT = "\\{\\s*%s\\s*:?\\s*\\[(.*)\\]}";

    public ObjectReader(Class<T> clazz) {
        this.clazz = clazz;
    }

    /** @param json: ""id": 1, "name": "std1"" */
    private T readObject(String json) {
        try {
            final Object result = clazz.newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(JsonValue.class)) {
                    String name = getName(field).toString();
                    Matcher matcher = Pattern.compile(String.format(VALUE_RGX_FORMAT, name)).matcher(json);
                    if (matcher.find()) setFieldValue(result, field, matcher.group(1));
                }
            }
            return clazz.newInstance().equals(result) ? null : clazz.cast(result);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /** @param json: "{"students": [{"id": 1,"name": "std1"},{"id": 2,"name": "std2"},{"id": 3,"name": "std3"}]}" */
    public Set<T> read(String json) {
        Set<T> set = new HashSet<>();
        if (json.length() != 0) {
            String rootNameVal = getClassName().toString();
            if (!rootNameVal.isEmpty()) rootNameVal = "\\s*" + rootNameVal;

            String arrNameVal = getArrayName().toString();
            if (!arrNameVal.isEmpty()) arrNameVal = "\"" + arrNameVal + "\"";

            Matcher matcher = Pattern.compile(String.format(ARRAY_NAME_RGX_FORMAT, arrNameVal)).matcher(json);
            if (matcher.find()) { // разбиение на массив объектов {"name": value},{...},...
                String[] split = trimFstLstSymbol(matcher.group(1).trim()).split("}\\s*," + rootNameVal + "\\s*\\{");
                Arrays.stream(split).forEach(str -> addToCollection(str, set));
            }
            else addToCollection(json, set);
        }
        return set;
    }

    private void addToCollection(String json, Collection<T> collection) {
        T obj = readObject(json);
        if (obj != null) collection.add(obj);
    }

    private String trimFstLstSymbol(String str) {
        char[] chars = str.toCharArray();
        if (chars.length == 0) return "";
        chars[0] = '\0';
        chars[chars.length-1] = '\0';
        return new String(chars);
    }

    private void setFieldValue(Object object, Field field, String value) throws IllegalAccessException {
        final Class<?> type = field.getType();
        field.setAccessible(true);
        if (String.class.isAssignableFrom(type)) field.set(object, value);
        else if (Long.class.isAssignableFrom(type)) field.set(object, Long.parseLong(value));
        else if (Character.class.isAssignableFrom(type)) field.set(object, value.toCharArray()[0]);
        else if (Boolean.class.isAssignableFrom(type)) field.set(object, Boolean.getBoolean(value));
        else if (Double.class.isAssignableFrom(type)) field.set(object, Double.parseDouble(value));
        else if (Integer.class.isAssignableFrom(type)) field.set(object, Integer.parseInt(value));
        else if (Float.class.isAssignableFrom(type)) field.set(object, Float.parseFloat(value));
        else if (Short.class.isAssignableFrom(type)) field.set(object, Short.parseShort(value));
        else if (Byte.class.isAssignableFrom(type)) field.set(object, Byte.parseByte(value));
    }
}
