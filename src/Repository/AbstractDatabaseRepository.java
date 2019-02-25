package Repository;

import Model.HasID;
import Validation.Validator;

import java.sql.*;

public abstract class AbstractDatabaseRepository<ID, E extends HasID<ID>> extends Repository<ID, E> {

    public AbstractDatabaseRepository(Validator<E> validator) {
        super(validator);
        loadAll();
    }

    public abstract void loadAll();

    protected Connection getLocalConnection(){
        try {
            return DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=ProiectFinalMAP;integratedSecurity=true");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public E save(E entity) {

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = getLocalConnection();
            if (connection != null) {
                if(super.save(entity) == null){
                    String saveSQL = saveEntityString(entity);
                    PreparedStatement prepsInsertProduct = connection.prepareStatement(saveSQL, Statement.RETURN_GENERATED_KEYS);
                    prepsInsertProduct.execute();
                    return null;
                }
            }
            return entity;
        } catch (ClassNotFoundException e) {
            return entity;
        } catch (SQLException e) {
            return entity;
        }
    }

    protected abstract String saveEntityString(E entity);

    @Override
    public E delete(ID id) {

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = getLocalConnection();
            if (connection != null) {
                E entity = super.delete(id);
                if(entity != null){
                    String deleteSQL = deleteEntityString(id);
                    PreparedStatement prepsInsertProduct = connection.prepareStatement(deleteSQL, Statement.RETURN_GENERATED_KEYS);
                    prepsInsertProduct.execute();
                }
                return entity;
            }
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        } catch (SQLException e) {
            return null;
        }
    }

    protected abstract String deleteEntityString(ID id);

    @Override
    public E update(E elem) {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = getLocalConnection();
            if (connection != null) {
                E entity = super.update(elem);
                if(entity == null){
                    String updateSQL = updateEntityString(elem);
                    PreparedStatement prepsInsertProduct = connection.prepareStatement(updateSQL, Statement.RETURN_GENERATED_KEYS);
                    prepsInsertProduct.execute();
                }
                return entity;
            }
            return elem;
        } catch (ClassNotFoundException e) {
            return elem;
        } catch (SQLException e) {
            return elem;
        }
    }

    protected abstract String updateEntityString(E elem);
}
