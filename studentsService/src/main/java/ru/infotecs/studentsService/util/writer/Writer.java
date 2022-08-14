package ru.infotecs.studentsService.util.writer;

/** Выводит переданную информацию */
public interface Writer {
    void writeMessage(String message);
    void writeErrMessage(String message, Exception e);
}
