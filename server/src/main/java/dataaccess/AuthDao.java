package dataaccess;

import model.AuthData;

import javax.xml.crypto.Data;

public interface AuthDao {
    void createAuth(AuthData auth) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(AuthData auth) throws DataAccessException;
    void clear();

}
