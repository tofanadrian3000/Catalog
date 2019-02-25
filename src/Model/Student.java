package Model;

import java.util.Objects;

public class Student implements HasID<Integer>{

    private Integer idStudent;
    private String name, group, email, professor;
    private boolean status;

    public Student(Integer idStudent, String name, String group, String email, String professor) {
        this.idStudent = idStudent;
        this.name = name;
        this.group = group;
        this.email = email;
        this.professor = professor;
        this.status = true;
    }

    public Student(Integer idStudent, String name, String group, String email, String professor, boolean status) {
        this.idStudent = idStudent;
        this.name = name;
        this.group = group;
        this.email = email;
        this.professor = professor;
        this.status = status;
    }

    public Student() {}

    @Override
    public Integer getID() {
        return idStudent;
    }

    @Override
    public void setID(Integer integer) {
        this.idStudent=integer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(idStudent, student.idStudent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idStudent);
    }

    @Override
    public String toString() {
        return  idStudent.toString() +"," + name +"," + group + "," + email + "," + professor + "," + status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Boolean getStatus(){
        return this.status;
    }
}
