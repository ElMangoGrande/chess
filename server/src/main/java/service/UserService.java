package service;

import dataaccess.DataAccessException;

import io.javalin.http.UnauthorizedResponse;
import model.*;

import dataaccess.UserDao;
import dataaccess.AuthDao;
import org.eclipse.jetty.http.BadMessageException;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class UserService {

    private static UserDao userDao;
    private static AuthDao authDao;

    public UserService(UserDao passedDao, AuthDao passedAuthDao) {
        userDao = passedDao;
        authDao = passedAuthDao;
    }
    public  String generateToken() {
        return UUID.randomUUID().toString();
    }

    public  RegistrationResult register(RegistrationRequest registerRequest) throws DataAccessException{
        if(registerRequest.username() == null){
            throw new BadMessageException("Error: no username provided");
        }
        if(registerRequest.password() == null){
            throw new BadMessageException("Error: no password provided");
        }
        if(registerRequest.email() == null){
            throw new BadMessageException("Error: no email provided");
        }
        userDao.createUser(new UserData(registerRequest.username(), registerRequest.password(),registerRequest.email()));
        AuthData authdata = new AuthData(registerRequest.username(),generateToken());
        authDao.createAuth(authdata);
        return new RegistrationResult(registerRequest.username(), authdata.authToken());
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException{

        if(loginRequest.username() == null){
            throw new BadMessageException("Error: no username provided");
        }
        if(loginRequest.password() == null){
            throw new BadMessageException("Error: no password provided");
        }
        //Gets the user
        UserData user = userDao.getUser(loginRequest.username());
        if(user == null){
            throw new UnauthorizedResponse("Error: invalid credentials");
        }
        if(!BCrypt.checkpw(loginRequest.password(), user.password())){
            throw new UnauthorizedResponse("Error: Invalid credentials");
        };
        //Creates new AuthToken
        AuthData authdata = new AuthData(loginRequest.username(),generateToken());
        authDao.createAuth(authdata);
        //Returns new loginResult
        return new LoginResult(loginRequest.username(), authdata.authToken());
    }

    public void logout(LogoutRequest logoutRequest) throws DataAccessException{
        AuthData data = authDao.getAuth(logoutRequest.authToken());
        authDao.deleteAuth(data);
    }

    public void clearUsers() throws DataAccessException {
        userDao.clear();
        authDao.clear();
    }

}
