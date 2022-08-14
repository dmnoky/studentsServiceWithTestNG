package ru.infotecs.studentsService.model;

import ru.infotecs.studentsService.util.json.annotation.JsonArrayName;
import ru.infotecs.studentsService.util.json.annotation.JsonRootName;
import ru.infotecs.studentsService.util.json.annotation.JsonValue;

import java.util.Comparator;
import java.util.Objects;

@JsonRootName("")
@JsonArrayName("students")
public class Student {
    public static final Comparator<Student> DEFAULT_STUDENT_COMPARATOR =
            Comparator.comparing(o -> o.getName().toLowerCase());

    @JsonValue()
    private Long id = 0L;
    @JsonValue()
    private String name = "";

    public Student() {
    }

    public Student(Long id) {
        this.id = id;
    }

    public Student(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return id.equals(student.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "id: " + id + ", name: '" + name + '\'';
    }
}
