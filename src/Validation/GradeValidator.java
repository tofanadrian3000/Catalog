package Validation;

import Model.Grade;

public class GradeValidator implements Validator<Grade> {
    @Override
    public void validate(Grade grade) throws ValidationException {
        String errors= new String();

        if(grade.getID()==null)
            errors+="An error has occurred!\n";
        if(grade.getID().getIdStudent()==null)
            errors+="The student that you've tried to assert the grade to is wrong!\n";
        if(grade.getID().getIdHomework()==null)
            errors+="The homework that you've tried to assert the grade for is wrong!\n";
        if(grade.getGrade()>10 || grade.getGrade()<1)
            errors+="The grade must be between 1 and 10!\n";
        if(grade.getFeedback()==null)
            errors+="Please, enter a feedback!";
        if(!errors.isEmpty())
            throw new ValidationException(errors);
    }
}
