package ru.infotecs.studentsService;

import ru.infotecs.studentsService.controller.StudentController;
import ru.infotecs.studentsService.dao.StudentDaoImpl;
import ru.infotecs.studentsService.model.Student;
import ru.infotecs.studentsService.util.ftp.FtpConnectJdk;
import ru.infotecs.studentsService.util.reader.ReaderFactory;
import ru.infotecs.studentsService.util.reader.exception.IncompleteOperationException;
import ru.infotecs.studentsService.util.writer.StringWriter;
import ru.infotecs.studentsService.util.writer.Writer;

import java.io.IOException;

public class Main {
    public static final String FILE_NAME = "students.json";
    public static boolean running = true;
    public static final Writer WRITER = new StringWriter();
    private static StudentController studentController;

    public static void main(String[] args) {
        WRITER.writeMessage("Старт...");
        try {
            try {
                String login = ReaderFactory.STRING_READER.read("Введите логин...");
                String password = ReaderFactory.STRING_READER.read("Введите пароль...");
                String address = ReaderFactory.STRING_READER.read("Введите адрес...");
                studentController = new StudentController(
                        new StudentDaoImpl(new FtpConnectJdk<>(login, password, address, FILE_NAME, Student.class))
                );
            } catch (IOException e) {
                WRITER.writeErrMessage("Ошибка открытия соеденения", e);
                running = false;
            }
            while (running) {
                try {
                    ReaderFactory.STRING_READER.read("Введите команду из /help...");
                } catch (IncompleteOperationException e) {
                    try {
                        ReaderFactory.READER.read("\n");
                    } catch (IncompleteOperationException ex) {
                        WRITER.writeMessage("Операция не может быть завершена, попробуйте снова.");
                    }
                }
            }
        } catch (IncompleteOperationException ex) {
            WRITER.writeErrMessage("Ошибка чтения", ex);
        }

        WRITER.writeMessage("Выход через...");
        for (int count = 10; count != 0; count--) {
            WRITER.writeMessage(String.valueOf(count));
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static StudentController getStudentController() {
        return studentController;
    }
}
