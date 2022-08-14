package ru.infotecs.studentsService.util.reader;

import ru.infotecs.studentsService.Main;
import ru.infotecs.studentsService.util.reader.exception.IncompleteOperationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

abstract class ReaderAbs<T> implements Reader<T> {
    private static final BufferedReader BUFFERED_READER = new BufferedReader(new InputStreamReader(System.in));
    public static final String COMMAND_HELP = "/help";
    protected static String lastOutput = "", lastInput = "";

    public abstract T read(String out) throws IncompleteOperationException;

    protected static boolean inputIsNotSystem(String consoleOut) throws IncompleteOperationException {
        Main.WRITER.writeMessage(consoleOut);
        lastOutput = consoleOut;
        try {
            String lastInput;
            while ((lastInput = BUFFERED_READER.readLine()).length() == 0) {
                Main.WRITER.writeMessage(lastOutput);
            }
            ReaderAbs.lastInput = lastInput;
            return checkSystemMessage(lastInput);
        } catch (IOException e) {
            Main.WRITER.writeErrMessage("Ошибка чтения консоли", e);
        }
        return false;
    }

    static boolean checkSystemMessage(String message) throws IncompleteOperationException {
        switch (message) {
            case COMMAND_HELP:
                Main.WRITER.writeMessage(
                        "'/add student' (Добавление студента (id генерируется автоматически))\n" +
                        "'/remove student' (Удаление студента по id)\n" +
                        "'/get student' (Получение информации о студенте по id)\n" +
                        "'/get students' (Получение списка студентов по имени)\n" +
                        "'/exit' (Завершение работы)");
                return inputIsNotSystem(lastOutput);
            case "/get students":
                Main.getStudentController().showList();
                return false;
            case "/get student":
                Main.getStudentController().show();
                return false;
            case "/add student":
                Main.getStudentController().add();
                return false;
            case "/remove student":
                Main.getStudentController().remove();
                return false;
            case "/exit": //5.	Завершение работы
                Main.running = false;
                return false;
        }
        return true;
    }
}
