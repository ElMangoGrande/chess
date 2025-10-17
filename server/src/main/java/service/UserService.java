package service;

import dataaccess.DataAccessException;

import io.javalin.http.UnauthorizedResponse;
import model.*;

import dataaccess.UserDao;
import dataaccess.AuthDao;
import org.eclipse.jetty.http.BadMessageException;

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

    public RegistrationResult register(RegistrationRequest registerRequest) throws DataAccessException{
        if(registerRequest.username() == null){
            throw new BadMessageException("Error: no username provided");
        }
        if(registerRequest.password() == null){
            throw new BadMessageException("Error: no password provided");
        }
        if(registerRequest.email() == null){
            throw new BadMessageException("Error: no email provided");
        }
        UserDao.createUser(new UserData(registerRequest.username(), registerRequest.password(),registerRequest.email()));
        AuthData authdata = new AuthData(registerRequest.username(),generateToken());
        AuthDao.createAuth(authdata);
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
        UserData user = UserDao.getUser(loginRequest.username());
        if(!loginRequest.password().equals(user.password())){
            throw new UnauthorizedResponse("Error: Invalid credentials");
        }
        //Creates new AuthToken
        AuthData authdata = new AuthData(loginRequest.username(),generateToken());
        AuthDao.createAuth(authdata);
        //Returns new loginResult
        return new LoginResult(loginRequest.username(), authdata.authToken());
    }

    public void logout(LogoutRequest logoutRequest) throws DataAccessException{
        AuthData data = AuthDao.getAuth(logoutRequest.authToken());
        AuthDao.deleteAuth(data);
    }

    public void clearUsers(){
        UserDao.clear();
        AuthDao.clear();
    }

}
