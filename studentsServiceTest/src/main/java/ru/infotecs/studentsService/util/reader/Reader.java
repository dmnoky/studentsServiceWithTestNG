package ru.infotecs.studentsService.util.reader;

import ru.infotecs.studentsService.util.reader.exception.IncompleteOperationException;

/** Считывает введенную информацию и конвертит её в указанный тип */
public interface Reader<T> {
    T read(String out) throws IncompleteOperationException;
}
