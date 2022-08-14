package ru.infotecs.studentsService.dao;

import ru.infotecs.studentsService.dao.exception.ListIsEmptyException;
import ru.infotecs.studentsService.dao.exception.NotFoundException;
import ru.infotecs.studentsService.model.Student;
import ru.infotecs.studentsService.util.ftp.FtpConnect;
import ru.infotecs.studentsService.util.ftp.exception.LoadException;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import static ru.infotecs.studentsService.model.Student.DEFAULT_STUDENT_COMPARATOR;

/**
 * Комментарии в интерфейсе.
 * Во всех методах список студентов всегда загружается заново, так как предпологается, что список изменяется не только нами.
 * */
public class StudentDaoImpl implements StudentDao {
    private final FtpConnect<Student> connect;

    public StudentDaoImpl(FtpConnect<Student> connect) {
        this.connect = connect;
    }

    /** Добавление студента (id генерируется автоматически) */
    @Override
    public void add(Student student) throws LoadException {
        Set<Student> students = connect.download();
        student.setId(getNextId(students)); // уникальный айди
        students.add(student);
        connect.upload(students);
    }

    /** Удаление студента по id
     * Предпологается, что список, получаемый из внешнего источника, хранит уникальные значения по Id
     */
    @Override
    public void remove(long inpId) throws LoadException, NotFoundException, ListIsEmptyException {
        Set<Student> students = connect.download();
        if (students != null && students.size() > 0) {
            if (!students.remove(new Student(inpId))) throw new NotFoundException();
            connect.upload(students);
        }
        else throw new ListIsEmptyException();
    }

    /** Получение информации о студенте по id */
    @Override
    public Student get(long inpId) throws NotFoundException, ListIsEmptyException, LoadException {
        Set<Student> students = connect.download();
        if (students != null && students.size() > 0) {
            Optional<Student> student = students.stream().filter(o -> o.getId() == inpId).findFirst();
            if (student.isPresent()) return student.get();
            else throw new NotFoundException();
        }
        else throw new ListIsEmptyException();
    }

    /** Получение списка студентов по имени.
     * TreeSet + hashCode по Id обеспечивают уникальность. */
    @Override
    public TreeSet<Student> getSet() throws LoadException {
        // Не понял условие "Получение списка студентов по имени" - имелось ввиду "Поиск по имени"?
        // Но тогда зачем условие "Список студентов при выводе отсортирован по алфавиту"?
        TreeSet<Student> result = new TreeSet<>(DEFAULT_STUDENT_COMPARATOR); // сортировка как дефолт
        result.addAll(connect.download());
        return result; //.stream().filter(o -> o.getName().equals(inpName)) Поиск по имени закомментил, оставил только сортировку
    }

    /** Предпологается, что "максимальный айди из списка" < Long.MAX_VALUE
     * @return "максимальный айди из списка" + 1 */
    public static long getNextId(Set<Student> students) {
        long nextId = 1;
        if (students != null && students.size() > 0)
            nextId = students.stream().max(Comparator.comparing(Student::getId)).get().getId() + 1;
        return nextId;
    }

}
