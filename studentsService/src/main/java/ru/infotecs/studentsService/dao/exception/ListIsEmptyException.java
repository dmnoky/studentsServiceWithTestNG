package ru.infotecs.studentsService.dao.exception;

public class ListIsEmptyException extends Exception {
    public ListIsEmptyException() {
        super();
    }
    public ListIsEmptyException(Exception e) {
        super(e);
    }
}
