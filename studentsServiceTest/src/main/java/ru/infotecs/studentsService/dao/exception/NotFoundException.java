package ru.infotecs.studentsService.dao.exception;

public class NotFoundException extends Exception {
    public NotFoundException() {
        super();
    }
    public NotFoundException(Exception e) {
        super(e);
    }
}
