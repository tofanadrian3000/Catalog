package Validation;

import Model.Student;
import org.apache.commons.text.WordUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StudentValidator implements Validator<Student>{

    @Override
    public void validate(Student student) throws ValidationException {
        String errors = new String();
        Pattern validName = Pattern.compile("^[A-Za-z ]+$");

        if(student.getName()==null || student.getName().isEmpty())
            errors+="The name of the student is empty!\n";
        else{
            Matcher matcher = validName.matcher(student.getName());
            if(!matcher.find())
                errors+="The name must contains only letters!\n";
            else {
                String newName = (String) WordUtils.capitalizeFully(student.getName(), ' ');
                student.setName(newName);
            }
        }

        if(student.getGroup()==null || student.getGroup().isEmpty())
            errors+="The group of the student is empty!\n";
        else{
            try{
                Integer.parseInt(student.getGroup());
            }
            catch(NumberFormatException e){
                errors+="The group must be an integer!\n";
            }
        }

        if(student.getEmail()==null || student.getEmail().isEmpty())
            errors+="The email of the student is empty!\n";
        else{
            Pattern validEmailAdressRegex = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
            Matcher matcher = validEmailAdressRegex.matcher(student.getEmail());
            if(matcher.find()==false)
                errors+="The email is incorrect!\n";
        }

        if(student.getProfessor()==null || student.getProfessor().isEmpty())
            errors+="The professor of the student is empty\n";
        else{
            Matcher matcher = validName.matcher(student.getProfessor());
            if(matcher.find()==false)
                errors+="The professor name must contains only letters!\n";
            else{
                String newProfessor = (String) WordUtils.capitalizeFully(student.getProfessor(), ' ');
                student.setProfessor(newProfessor);
            }
        }

        if(!errors.isEmpty()) throw new ValidationException(errors);
        }
}