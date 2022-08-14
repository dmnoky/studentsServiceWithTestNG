package ru.infotecs.studentsService.util.json.mapper;

import ru.infotecs.studentsService.util.json.annotation.JsonArrayName;
import ru.infotecs.studentsService.util.json.annotation.JsonRootName;
import ru.infotecs.studentsService.util.json.annotation.JsonValue;

import java.lang.reflect.Field;

abstract class Mapper<T> {
    protected Class<T> clazz;

    protected StringBuilder getArrayName() {
        StringBuilder result = new StringBuilder();
        final JsonArrayName anRN = clazz.getAnnotation(JsonArrayName.class);
        if (anRN != null) result.append(clazz.getAnnotation(JsonArrayName.class).value());
        return result;
    }

    protected StringBuilder getClassName() {
        StringBuilder result = new StringBuilder();
        String rootNameVal = "";
        final JsonRootName anRN = clazz.getAnnotation(JsonRootName.class);
        if (anRN != null) rootNameVal = clazz.getAnnotation(JsonRootName.class).value();
        if (!rootNameVal.isEmpty()) result.append(wrapString(rootNameVal)).append(':');
        return result;
    }

    protected StringBuilder getField(final Field field, final T object) throws NoSuchFieldException, IllegalAccessException {
        StringBuilder result = new StringBuilder(getName(field));
        result.append(": ").append(getValue(field, object));
        return result;
    }

    protected StringBuilder getValue(final Field field, final T object) throws IllegalAccessException {
        final Class<?> type = field.getType();
        field.setAccessible(true);
        final String value = field.get(object).toString();
        if (isNotString(type)) return new StringBuilder(value);
        else return wrapString(value);
    }

    protected StringBuilder getName(final Field field) {
        JsonValue an = field.getAnnotation(JsonValue.class);
        String name = an.value();
        if (name.isEmpty()) name = field.getName();
        return wrapString(name);
    }

    protected boolean isNotString(Class<?> type) {
        if (type.isPrimitive()) return !type.getName().equals("char");
        else return Number.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type);
    }

    protected StringBuilder wrapString(String str) {
        return new StringBuilder().append('\"').append(str).append('\"');
    }
}
