package dataaccess;

import model.RegistrationRequest;
import model.UserData;

import java.util.HashSet;
import java.util.Set;

public class MemoryUserDAO implements UserDao{
    private static final Set<UserData> USER_DATA = new HashSet<>();

    @Override
    public void createUser(RegistrationRequest registrationRequest) throws DataAccessException{
        String newUsername = registrationRequest.username();
        for(UserData user : USER_DATA){
            if(user.username().equals(newUsername)){
                throw new DataAccessException("Error: username already in use");
            }
        }
        UserData newUser = new UserData(newUsername, registrationRequest.password(), registrationRequest.email());
        USER_DATA.add(newUser);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException{
        for(UserData user : USER_DATA){
            if(user.username().equals(username)){
                return user;
            }
        }
        throw new DataAccessException("Error: User does not exist");
    }

    @Override
    public void clear(){
        USER_DATA.clear();
    }

}
