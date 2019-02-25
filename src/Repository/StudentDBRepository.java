package Repository;

import Model.Student;
import Validation.Validator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentDBRepository extends AbstractDatabaseRepository<Integer, Student> {
    public StudentDBRepository(Validator<Student> validator) {
        super(validator);
    }

    @Override
    public void loadAll() {
        Connection connection = getLocalConnection();
        ResultSet resultSet = null;
        try{
            resultSet = connection.prepareStatement("Select * from Students").executeQuery();
            while(resultSet.next()){
                super.save(new Student(resultSet.getInt(1),
                                resultSet.getString(2),
                                resultSet.getString(3),
                                resultSet.getString(4),
                                resultSet.getString(5),
                                resultSet.getBoolean(6)));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String saveEntityString(Student student) {
        String status;
        if(student.getStatus().equals(true))
            status="1";
        else status="0";

        return "INSERT INTO Students VALUES " + "(" +
                student.getID().toString() + ", " +
                "'" + student.getName() + "', " +
                "'" + student.getGroup() + "', " +
                "'" + student.getEmail() + "', " +
                "'" + student.getProfessor() + "', " +
                status + ")";
    }

    @Override
    protected String deleteEntityString(Integer id) {
        return "DELETE FROM Students WHERE IdStudent="+id.toString();
    }

    @Override
    protected String updateEntityString(Student student) {
        String status;
        if(student.getStatus().equals(true))
            status="1";
        else status="0";

        return "UPDATE Students SET Name='" + student.getName() +
                "', GroupStudent='" + student.getGroup() +
                "', Email='" + student.getEmail() +
                "', Professor='" + student.getProfessor() +
                "', StatusStudent=" + status +
                " WHERE IdStudent=" + student.getID().toString();
    }


}
