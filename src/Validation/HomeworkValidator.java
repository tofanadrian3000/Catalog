package Validation;

import Model.Homework;

public class HomeworkValidator implements Validator<Homework> {

    @Override
    public void validate(Homework homework) throws ValidationException {
        String errors = "";
        if(homework.getID()==null)
            errors+="Homework number is empty!\n";
        if (homework.getReceived() == null)
            errors += "The received week of the homework is empty!\n";
        if (homework.getReceived() != null && homework.getReceived() < 1 || homework.getReceived() > 14)
            errors += "The received week must be between 1 and 14\n";
        if (homework.getDeadline() == null || homework.getDeadline() < 1 || homework.getDeadline() > 14 || homework.getDeadline() < homework.getReceived())
            errors += "The deadline of the homework is empty!\n";
        if (homework.getDeadline() != null && homework.getDeadline() < 1 || homework.getDeadline() > 14)
            errors += "The deadline week must be between 1 and 14\n";
        if (homework.getDescription() == null || homework.getDescription().isEmpty())
            errors += "The description of the homework is empty!\n";
        if (!errors.isEmpty()) throw new ValidationException(errors);
    }

}
