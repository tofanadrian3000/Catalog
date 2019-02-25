package Repository;

import Model.Grade;
import Model.GradeId;
import Validation.Validator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class GradeDBRepository extends AbstractDatabaseRepository<GradeId, Grade> {
    public GradeDBRepository(Validator<Grade> validator) {
        super(validator);
    }

    @Override
    public void loadAll() {
        Connection connection = getLocalConnection();
        ResultSet resultSet = null;
        try{
            resultSet = connection.prepareStatement("Select * from Grades").executeQuery();
            while(resultSet.next()){
                super.save(new Grade( resultSet.getInt(1),
                        resultSet.getInt(2),
                        resultSet.getDouble(3),
                        resultSet.getDate(4).toLocalDate(),
                        resultSet.getString(5)));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String saveEntityString(Grade grade) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return "INSERT INTO Grades VALUES " + "(" +
                grade.getID().getIdStudent().toString() + ", " +
                grade.getID().getIdHomework().toString() + ", " +
                String.valueOf(grade.getGrade()) +
                ",'" + grade.getData().format(dateTimeFormatter) + "', " +
                "'" + grade.getFeedback()+ "')";
    }

    @Override
    protected String deleteEntityString(GradeId id) {
        return "DELETE FROM Grades WHERE IdStudent="+id.getIdStudent().toString() +
                "AND IdHomework="+id.getIdHomework().toString();
    }

    @Override
    protected String updateEntityString(Grade grade) {
        return "UPDATE Homeworks SET Grade='" + String.valueOf(grade.getGrade()) +
                "', Data='" + grade.getData().toString() +
                "', Feedback='" + grade.getFeedback() + "'" +
                " WHERE IdStudent=" + grade.getID().getIdStudent().toString() +
                " AND IdHomework=" + grade.getID().getIdHomework().toString();
    }
}
