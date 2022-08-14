package ru.infotecs.studentsService;

import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import ru.infotecs.studentsService.dao.StudentDaoImpl;
import ru.infotecs.studentsService.dao.exception.ListIsEmptyException;
import ru.infotecs.studentsService.dao.exception.NotFoundException;
import ru.infotecs.studentsService.model.Student;
import ru.infotecs.studentsService.util.ftp.FtpConnectJdk;
import ru.infotecs.studentsService.util.ftp.exception.LoadException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.TreeSet;

@Test(testName = "Student Controller")
public class StudentControllerTest {
    private static final String
            USER_NAME = "user",
            USER_PASS = "password",
            ADDRESS = "localhost",
            DIRECTORY = "/data",
            FILE_NAME = "students.json";

    /** Дефолтный список для сервера */
    private static final byte[] JSON_CONTENT = ("{\"students\" : [" +
                "{ \"id\": 1, \"name\": \"Student1\" }," +
                "{ \"id\": 2, \"name\": \"Student2\" }," +
                "{ \"id\": 3, \"name\": \"Student3\" }" +
            "]}").getBytes(StandardCharsets.UTF_8);

    /** Дефолтная запись файловой системы, которая хранит JSON_CONTENT */
    private static final FileEntry fileEntry = new FileEntry(DIRECTORY+"/"+FILE_NAME);
    private StudentDaoImpl studentDao;
    private FakeFtpServer fakeFtpServer;

    @BeforeSuite(description = "Поднятие сервака и DAO")
    public void startup() throws IOException {
        fakeFtpServer = new FakeFtpServer();
        fakeFtpServer.addUserAccount(new UserAccount(USER_NAME, USER_PASS, DIRECTORY));

        FileSystem fileSystem = new UnixFakeFileSystem();
        fileSystem.add(new DirectoryEntry(DIRECTORY));
        fileSystem.add(fileEntry);
        fakeFtpServer.setFileSystem(fileSystem);
        //fakeFtpServer.setServerControlPort(0);
        fakeFtpServer.start();

        studentDao = new StudentDaoImpl(new FtpConnectJdk<>(USER_NAME, USER_PASS, ADDRESS, FILE_NAME, Student.class));
    }

    @BeforeMethod(description = "Обновляет список студентов на сервере, перед каждым тест-методом")
    public void reloadList() {
        fileEntry.setContents(JSON_CONTENT);
    }

    @Test(description = "Отсортирован ли список по имени (ASC), получаемый с сервера")
    public void rowsIsSortedByName() throws LoadException {
        clearList();
        // инициализация списка в отсортированном порядке
        LinkedHashSet<Student> sortedSet = new LinkedHashSet<Student>(){{
            add(new Student(1L, "1"));
            add(new Student(2L, "2"));
            add(new Student(3L, "3"));
            add(new Student(4L, "4"));
            add(new Student(5L, "5"));
            add(new Student(6L, "6"));
            add(new Student(7L, "7"));
        }};
        // Поштучно грузим те-же записи на сервер в другом порядке
        // добавляем новую строку
        Student student = new Student();
        student.setName("4");
        studentDao.add(student);
        // добавляем новую строку
        student.setName("5");
        studentDao.add(student);
        // добавляем новую строку
        student.setName("6");
        studentDao.add(student);
        // добавляем новую строку
        student.setName("3");
        studentDao.add(student);
        // добавляем новую строку
        student.setName("1");
        studentDao.add(student);
        // добавляем новую строку
        student.setName("2");
        studentDao.add(student);
        // добавляем новую строку
        student.setName("7");
        studentDao.add(student);
        // загружаем обновленный список студентов
        TreeSet<Student> studentsNew = studentDao.getSet();
        Assert.assertEquals(sortedSet.size(), studentsNew.size()); // одиниковый размер списков
        boolean isSorted = true;
        for (Iterator<Student> iNew = studentsNew.iterator(), iSort = sortedSet.iterator(); iNew.hasNext(); ) {
            if (!iNew.next().getName().equals(iSort.next().getName())) {
                isSorted = false;
                break;
            }
        }
        Assert.assertTrue(isSorted); // списки отсортированны по имени
    }

    @Test(description = "Сохраняются ли новые записи на сервер + ещё проверка на сортировку по имени (ASC)")
    public void addRowsThenCheckListSizeAndSortedByName() throws LoadException {
        TreeSet<Student> students = studentDao.getSet(); // загружаем дефолтный список студентов
        final int size = students.size();
        final Student fstStudentFromList = students.first(); // первый студент из отсортированного списка
        final Student lstStudentFromList = students.last(); // первый студент из отсортированного списка
        Student newFstStudent = new Student();
        newFstStudent.setName(setNextCharStr(fstStudentFromList.getName(), -1, 0)); // первый символ - 1
        studentDao.add(newFstStudent); // добавляем новую строку (должна быть в начале)
        Student newLstStudent = new Student();
        newLstStudent.setName(setNextCharStr(lstStudentFromList.getName(), +1, 0)); // первый символ + 1
        studentDao.add(newLstStudent); // добавляем новую строку (должна быть в конце)
        students = studentDao.getSet(); // загружаем обновленный список студентов
        Assert.assertEquals(students.size(), size+2); // новый размер = старому размеру + 2 новые строки
        Assert.assertNotEquals(fstStudentFromList, students.first()); // предыдущий первый объект из списка != текущему
        Assert.assertEquals(newFstStudent.getName(), students.first().getName()); // имя первого объекта из списка = последнему добавленному
        Assert.assertNotEquals(lstStudentFromList, students.last()); // предыдущий первый объект из списка != текущему
        Assert.assertEquals(newLstStudent.getName(), students.last().getName()); // имя последнего объекта из списка = последнему добавленному
    }

    @Test(description = "Удаляется ли существующая запись")
    public void removeExistRowThenCheckListSize() throws LoadException, NotFoundException, ListIsEmptyException {
        final int size = studentDao.getSet().size(); // размер дефолтного списка студентов
        studentDao.remove(1L);
        Assert.assertEquals(size - 1, studentDao.getSet().size());
    }

    @Test(description = "Отображается ли существующая и новая запись")
    public void getExistRows() throws ListIsEmptyException, NotFoundException, LoadException {
        studentDao.get(1); // есть дефолтная запись
        final long newId = StudentDaoImpl.getNextId(studentDao.getSet());
        final String newName = "newRow";
        studentDao.add(new Student(newId, newName));
        Assert.assertEquals(studentDao.get(newId).getName(), newName); // есть новая запись
    }

    @Test(  description = "Ожидаем ошибку удаления записи, так как лист пуст",
            expectedExceptions = ListIsEmptyException.class)
    public void removeRowEmptyListExpectedErr() throws LoadException, NotFoundException, ListIsEmptyException {
        clearList();
        studentDao.remove(1L);
    }

    @Test(  description = "Ожидаем ошибку поиска записи, так как лист пуст",
            expectedExceptions = ListIsEmptyException.class)
    public void findRowEmptyListExpectedErr() throws ListIsEmptyException, NotFoundException, LoadException {
        clearList();
        studentDao.get(1L);
    }

    @Test(  description = "Ожидаем ошибку удаления записи, так как её нет в листе",
            expectedExceptions = NotFoundException.class)
    public void removeRowNotFoundExpectedErr() throws ListIsEmptyException, NotFoundException, LoadException {
        studentDao.remove(11L);
    }

    @Test(  description = "Ожидаем ошибку поиска записи, так как её нет в листе",
            expectedExceptions = NotFoundException.class)
    public void findRowNotFoundRowExpectedErr() throws ListIsEmptyException, NotFoundException, LoadException {
        studentDao.get(11L);
    }

    @AfterSuite(description = "Выключает сервер")
    public void shutdown() {
        fakeFtpServer.stop();
    }

    /** Удаляет данные с сервера */
    private void clearList() throws LoadException {
        fileEntry.setContents(""); // удаляем данные с сервера
        Assert.assertEquals(studentDao.getSet().size(), 0); // список обнулился
    }

    /**@param str - обрабатываемая строка.
     * @param indexChar - номер символа в str, который будет изменен.
     * @param shiftChar - смещает символ на переданное число (shiftChar != 0) в str на позиции indexChar.
     * @return возвращает обработанный str
     * */
    private String setNextCharStr(String str, int shiftChar, int indexChar) {
        if (shiftChar == 0 || indexChar < 0 || indexChar >= str.length()) return str;
        return str.substring(0, indexChar) +
                (char) (str.charAt(indexChar) + shiftChar) + str.substring(indexChar + 1);
    }
}
