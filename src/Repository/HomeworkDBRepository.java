package Repository;

import Model.Homework;
import Validation.Validator;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HomeworkDBRepository extends AbstractDatabaseRepository<Integer, Homework> {
    public HomeworkDBRepository(Validator<Homework> validator) {
        super(validator);
    }

    @Override
    public void loadAll() {
        Connection connection = getLocalConnection();
        ResultSet resultSet = null;
        try{
            resultSet = connection.prepareStatement("Select * from Homeworks").executeQuery();
            while(resultSet.next()){
                super.save(new Homework(resultSet.getInt(1),
                        resultSet.getInt(2),
                        resultSet.getInt(3),
                        resultSet.getString(4)));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String saveEntityString(Homework homework) {
        return "INSERT INTO Homeworks VALUES " + "(" +
                homework.getID().toString() + ", " +
                "'" + homework.getReceived().toString() + "', " +
                "'" + homework.getDeadline().toString() + "', " +
                "'" + homework.getDescription() + "')";
    }

    @Override
    protected String deleteEntityString(Integer id) {
        return "DELETE FROM Homeworks WHERE IdHomework="+id.toString();
    }

    @Override
    protected String updateEntityString(Homework homework) {
        return "UPDATE Homeworks SET Received='" + homework.getReceived().toString() +
                "', Deadline='" + homework.getDeadline().toString() +
                "', Description='" + homework.getDescription() + "'" +
                " WHERE IdHomework=" + homework.getID().toString();
    }
}
