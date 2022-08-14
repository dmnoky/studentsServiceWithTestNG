package ru.infotecs.studentsService.util.writer;

public class StringWriter implements Writer {
    @Override
    public void writeMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void writeErrMessage(String message, Exception e) {
        System.err.println(message);
        if (e != null) e.printStackTrace();
    }
}
