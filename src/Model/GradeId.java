package Model;

import java.util.Objects;

public class GradeId {
    private Integer idStudent;
    private Integer idHomework;

    public GradeId(Integer idStudent, Integer idHomework) {
        this.idStudent = idStudent;
        this.idHomework = idHomework;
    }

    public Integer getIdStudent() {
        return idStudent;
    }

    public void setIdStudent(Integer idStudent) {
        this.idStudent = idStudent;
    }

    public Integer getIdHomework() {
        return idHomework;
    }

    public void setIdHomework(Integer idHomework) {
        this.idHomework = idHomework;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GradeId gradeId = (GradeId) o;
        return Objects.equals(idStudent, gradeId.idStudent) &&
                Objects.equals(idHomework, gradeId.idHomework);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idStudent, idHomework);
    }

    @Override
    public String toString() {
        return idStudent + "|" + idHomework;
    }
}
