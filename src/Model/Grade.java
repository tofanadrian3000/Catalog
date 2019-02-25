package Model;

import java.time.LocalDate;
import java.util.Objects;

public class Grade implements HasID<GradeId> {
    private GradeId idGrade;
    private double grade;
    private LocalDate data;
    private String feedback;

    public Grade(Integer idStudent, Integer idHomework, double grade, LocalDate data) {
        this.idGrade=new GradeId(idStudent,idHomework);
        this.grade = grade;
        this.data = data;
        this.feedback="";
    }

    public Grade(Integer idStudent, Integer idHomework, double grade, LocalDate data, String feedback) {
        this.idGrade = new GradeId(idStudent,idHomework);
        this.grade = grade;
        this.data = data;
        this.feedback = feedback;
    }

    public static Integer getRoundedGrade(Double grade){
        Integer roundedGrade = grade.intValue();
        if(grade-roundedGrade>=0.5)
            return roundedGrade+1;
        else return roundedGrade;
    }

    public Integer getRoundedGrade(){
        Integer roundedGrade = Integer.parseInt(String.valueOf(grade));
        if(grade-roundedGrade>=0.5)
            return roundedGrade+1;
        else return roundedGrade;
    }

    @Override
    public GradeId getID() {
        return idGrade;
    }

    @Override
    public void setID(GradeId id) {
        this.idGrade = id;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grade grade = (Grade) o;
        return Objects.equals(idGrade, grade.idGrade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idGrade);
    }

    @Override
    public String toString() {
        return idGrade + "|"+ grade + "|" + data +"|"+ feedback;
    }

}
