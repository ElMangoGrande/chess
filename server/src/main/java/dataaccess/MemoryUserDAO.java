package dataaccess;

import model.RegistrationRequest;
import model.UserData;

import java.util.HashSet;
import java.util.Set;

public class MemoryUserDAO implements UserDao{
    private static final Set<UserData> USER_DATA = new HashSet<>();

    @Override
    public void createUser(UserData user) throws DataAccessException{
        if(USER_DATA.contains(user)) {
            throw new DataAccessException("Error: username already exists");
        }
        USER_DATA.add(user);
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
