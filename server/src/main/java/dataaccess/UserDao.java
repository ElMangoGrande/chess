package dataaccess;

import model.RegistrationRequest;
import model.UserData;

public interface UserDao {
    void createUser(RegistrationRequest registrationRequest) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void clear();
}
