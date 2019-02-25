package DTO;

import Model.Grade;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentGradeDTO {
    private Integer studentId;
    private String studentName;
    private List<Grade> studentGrades;

    public StudentGradeDTO(Integer studentId, String studentName, List<Grade> studentGrades) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentGrades = studentGrades;
    }
    public Integer getStudentId() {
        return studentId;
    }


    public String getName() {
        return studentName;
    }

    public String getGradeH1(){
        Optional<Grade> o=studentGrades.stream().filter(x->x.getID().getIdHomework()==1).findFirst();
        if(o.isPresent()){
            return String.valueOf(o.get().getGrade());
        }
        else return null;
    }
    public String getGradeH2(){
        Optional<Grade> o=studentGrades.stream().filter(x->x.getID().getIdHomework()==2).findFirst();
        if(o.isPresent()){
            return String.valueOf(o.get().getGrade());
        }
        else return null;
    }
    public String getGradeH3(){
        Optional<Grade> o=studentGrades.stream().filter(x->x.getID().getIdHomework()==3).findFirst();
        if(o.isPresent()){
            return String.valueOf(o.get().getGrade());
        }
        else return null;
    }
    public String getGradeH4(){
        Optional<Grade> o=studentGrades.stream().filter(x->x.getID().getIdHomework()==4).findFirst();
        if(o.isPresent()){
            return String.valueOf(o.get().getGrade());
        }
        else return null;
    }
    public String getGradeH5(){
        Optional<Grade> o=studentGrades.stream().filter(x->x.getID().getIdHomework()==5).findFirst();
        if(o.isPresent()){
            return String.valueOf(o.get().getGrade());
        }
        else return null;
    }
    public String getGradeH6(){
        Optional<Grade> o=studentGrades.stream().filter(x->x.getID().getIdHomework()==6).findFirst();
        if(o.isPresent()){
            return String.valueOf(o.get().getGrade());
        }
        else return null;
    }
    public String getGradeH7(){
        Optional<Grade> o=studentGrades.stream().filter(x->x.getID().getIdHomework()==7).findFirst();
        if(o.isPresent()){
            return String.valueOf(o.get().getGrade());
        }
        else return null;
    }
    public String getGradeH8(){
        Optional<Grade> o=studentGrades.stream().filter(x->x.getID().getIdHomework()==8).findFirst();
        if(o.isPresent()){
            return String.valueOf(o.get().getGrade());
        }
        else return null;
    }
    public String getGradeH9(){
        Optional<Grade> o=studentGrades.stream().filter(x->x.getID().getIdHomework()==9).findFirst();
        if(o.isPresent()){
            return String.valueOf(o.get().getGrade());
        }
        else return null;
    }
    public String getGradeH10(){
        Optional<Grade> o=studentGrades.stream().filter(x->x.getID().getIdHomework()==10).findFirst();
        if(o.isPresent()){
            return String.valueOf(o.get().getGrade());
        }
        else return null;
    }
    public String getGradeH11(){
        Optional<Grade> o=studentGrades.stream().filter(x->x.getID().getIdHomework()==11).findFirst();
        if(o.isPresent()){
            return String.valueOf(o.get().getGrade());
        }
        else return null;
    }
    public String getGradeH12(){
        Optional<Grade> o=studentGrades.stream().filter(x->x.getID().getIdHomework()==12).findFirst();
        if(o.isPresent()){
            return String.valueOf(o.get().getGrade());
        }
        else return null;
    }
    public String getGradeH13(){
        Optional<Grade> o=studentGrades.stream().filter(x->x.getID().getIdHomework()==13).findFirst();
        if(o.isPresent()){
            return String.valueOf(o.get().getGrade());
        }
        else return null;
    }
    public String getGradeH14(){
        Optional<Grade> o=studentGrades.stream().filter(x->x.getID().getIdHomework()==14).findFirst();
        if(o.isPresent()){
            return String.valueOf(o.get().getGrade());
        }
        else return null;
    }
}