package ru.infotecs.studentsService.dao;

import ru.infotecs.studentsService.dao.exception.ListIsEmptyException;
import ru.infotecs.studentsService.dao.exception.NotFoundException;
import ru.infotecs.studentsService.model.Student;
import ru.infotecs.studentsService.util.ftp.exception.LoadException;

import java.util.TreeSet;

/** Обеспечивает взаимодейсвтие с сервером, где хранится информация о студентах */
public interface StudentDao {

    /** Добавление студента
     * @param student - предзаполенная запись
     * @throws LoadException - ошибка FTP загрузки
     * */
    void add(Student student) throws LoadException;

    /** Удаление студента по id
     * Предпологается, что список, получаемый из внешнего источника, хранит уникальные значения по Id
     * @param inpId - айди записи
     * @throws NotFoundException - записи с таким айди нет в системе
     * @throws ListIsEmptyException - записей нет
     * @throws LoadException - ошибка FTP загрузки
     * */
    void remove(long inpId) throws LoadException, NotFoundException, ListIsEmptyException;

    /** Получение информации о студенте по id
     * @param inpId - айди записи
     * @throws NotFoundException - записи с таким айди нет в системе
     * @throws ListIsEmptyException - записей нет
     * @throws LoadException - ошибка FTP загрузки
     * @return найденная запись
     * */
    Student get(long inpId) throws LoadException, NotFoundException, ListIsEmptyException;

    /** Получение списка студентов
     * @throws LoadException - ошибка FTP загрузки
     * @return Список студентов отсортированый по алфавиту */
    TreeSet<Student> getSet() throws LoadException;

}
