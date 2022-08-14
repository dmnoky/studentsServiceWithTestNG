package ru.infotecs.studentsService.controller;

import ru.infotecs.studentsService.Main;
import ru.infotecs.studentsService.dao.StudentDao;
import ru.infotecs.studentsService.dao.exception.ListIsEmptyException;
import ru.infotecs.studentsService.dao.exception.NotFoundException;
import ru.infotecs.studentsService.model.Student;
import ru.infotecs.studentsService.util.ftp.exception.LoadException;
import ru.infotecs.studentsService.util.reader.ReaderFactory;
import ru.infotecs.studentsService.util.reader.exception.IncompleteOperationException;

public class StudentController {
    private final StudentDao studentDao;

    public StudentController(StudentDao studentDao) {
        this.studentDao = studentDao;
    }

    /** '/add student' (Добавление студента (id генерируется автоматически)) */
    public void add() {
        try {
            Student student = new Student();
            student.setName(ReaderFactory.STRING_READER.read("Введите имя..."));
            studentDao.add(student);
        } catch (IncompleteOperationException | LoadException ignore) { /* NOP */ }
    }

    /** '/remove student' (Удаление студента по id)
     * Предпологается, что список, получаемый из внешнего источника, хранит уникальные значения по Id
     * */
    public void remove() {
        try {
            long inpId = ReaderFactory.LONG_READER.read("Введите id...");
            studentDao.remove(inpId);
        }
        catch (IncompleteOperationException | LoadException ignore) { /* NOP */ }
        catch (ListIsEmptyException e) {
            Main.WRITER.writeMessage("Список студентов пуст");
        } catch (NotFoundException e) {
            Main.WRITER.writeMessage("Студент не найден в списке");
        }
    }

    /** '/get student' (Получение информации о студенте по id) */
    public void show() {
        try {
            long inpId = ReaderFactory.LONG_READER.read("Введите id...");
            Main.WRITER.writeMessage(studentDao.get(inpId).toString());
        }
        catch (IncompleteOperationException | LoadException ignore) { /* NOP */ }
        catch (ListIsEmptyException e) {
            Main.WRITER.writeMessage("Список студентов пуст");
        } catch (NotFoundException e) {
            Main.WRITER.writeMessage("Студент не найден в списке");
        }
    }

    /** '/get students' (Получение списка студентов по имени)
     * Список студентов при выводе отсортирован по алфавиту */
    public void showList() {
        try {
            //Пояснение коммента в StudentDaoImpl().getSet()
            //String inpName = ReaderFactory.STRING_READER.read("Введите имя...");
            Main.WRITER.writeMessage("\tСписок студентов:");
            studentDao.getSet().forEach(o -> Main.WRITER.writeMessage(o.toString()));
        } catch (LoadException ignore) { /* NOP */ }
    }
}
