package dataaccess;

import io.javalin.http.UnauthorizedResponse;
import model.AuthData;
import model.UserData;
import service.AlreadyTakenException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthSQL implements AuthDao{

    @Override
    public void createAuth(AuthData auth) throws DataAccessException{
        String authToken = auth.authToken();
        String username = auth.username();
        if(getAuth(authToken)!= null){
            throw new AlreadyTakenException("Error: AuthData already exists");
        }
        String updateStatement = "INSERT INTO AuthData(authToken,username) VALUES(?,?)";
        DatabaseManager.executeUpdate(updateStatement,authToken,username);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException{
        String selectStatement = "SELECT * FROM AuthData WHERE authToken = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(selectStatement)) {
                preparedStatement.setString(1, authToken);
                try(ResultSet rs = preparedStatement.executeQuery()){
                    if(rs.next()){
                        return new AuthData(rs.getString("username"),
                                rs.getString("authToken"));
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
    public void deleteAuth(AuthData auth) throws DataAccessException{
        String deleteStatement = "DELETE FROM AuthData WHERE authToken = ?";
        if(auth == null){
            throw new UnauthorizedResponse("Error: no auth provided");
        }
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(deleteStatement)) {

                preparedStatement.setString(1, auth.authToken());
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected == 0) {
                    throw new DataAccessException("Error: No auth token found to delete");
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error deleting auth data", ex);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String updateStatement = "TRUNCATE AuthData";
        DatabaseManager.executeUpdate(updateStatement);
    }
}
