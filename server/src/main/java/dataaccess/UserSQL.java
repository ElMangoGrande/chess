package dataaccess;

import model.UserData;
import service.AlreadyTakenException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UserSQL implements UserDao{

    @Override
    public void createUser(UserData user) throws DataAccessException{
        String username = user.username();
        String password = user.password();
        String email = user.email();
        if(getUser(username)!= null){
            throw new AlreadyTakenException("Error: username already exists");
        }
            String updateStatement = "INSERT INTO UserData(username,password,email) VALUES(?,?,?)";
            DatabaseManager.executeUpdate(updateStatement,username,password,email);
    }
    @Override
    public UserData getUser(String username) throws DataAccessException{

        String selectStatement = "SELECT * FROM UserData WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
                try (var preparedStatement = conn.prepareStatement(selectStatement)) {
                    preparedStatement.setString(1, username);
                    try(ResultSet rs = preparedStatement.executeQuery()){
                        if(rs.next()){
                            return new UserData(rs.getString("username"),
                                    rs.getString("password"),
                                    rs.getString("email"));
                        }
                        else{
                            return null;
                        }
                    }
                }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: SQL tables didn't create");
        }
    }

    @Override
    public void clear() throws DataAccessException {
            String updateStatement = "TRUNCATE UserData";
            DatabaseManager.executeUpdate(updateStatement);
    }

}
