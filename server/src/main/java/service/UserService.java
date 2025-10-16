package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import model.*;

import dataaccess.UserDao;
import dataaccess.AuthDao;

import java.util.UUID;

public class UserService {

    private UserDao UserDao;
    private AuthDao AuthDao;

    public UserService(UserDao passedDao, AuthDao authDao) {
        UserDao = passedDao;
        AuthDao = authDao;
    }
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public RegistrationResult register(RegistrationRequest registerRequest) throws AlreadyTakenException{
            try{UserDao.createUser(registerRequest);}
            catch (DataAccessException ex){
                throw new AlreadyTakenException(ex.getMessage());
            }

            AuthData authdata = new AuthData(registerRequest.username(),generateToken());
            try{AuthDao.createAuth(authdata);}
            catch (DataAccessException e) {
                throw new AlreadyTakenException(e.getMessage());
            }
            return new RegistrationResult(registerRequest.username(), authdata.authToken());
    }

    public LoginResult login(LoginRequest loginRequest) {
        //Gets the user
        try{UserData user = UserDao.getUser(loginRequest.username());}
        catch (DataAccessException e) {
            throw new DoesNotExistException(e.getMessage());
        }
        //Creates new AuthToken
        AuthData authdata = new AuthData(loginRequest.username(),generateToken());
        try{AuthDao.createAuth(authdata);}
        catch (DataAccessException e) {
            throw new AlreadyTakenException(e.getMessage());
        }
        //Returns new loginResult
        return new LoginResult(loginRequest.username(), authdata.authToken());
    }

    public void logout(LogoutRequest logoutRequest) {
        try{
            AuthData data = AuthDao.getAuth(logoutRequest.authToken());
            AuthDao.deleteAuth(data);
        }
        catch (DataAccessException e) {
            throw new DoesNotExistException(e.getMessage());
        }

    }

}
