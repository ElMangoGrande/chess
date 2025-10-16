package dataaccess;

import model.AuthData;

import java.util.HashSet;
import java.util.Set;


public class MemoryAuthDAO implements AuthDao{
    private static final Set<AuthData> AUTH_DATA= new HashSet<>();

    @Override
    public void createAuth(AuthData auth) throws DataAccessException{
        if(AUTH_DATA.contains(auth)){
            throw new DataAccessException("Error: AuthToken exists already");
        }else{
            AUTH_DATA.add(auth);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        for(AuthData auth : AUTH_DATA){
            if(auth.authToken().equals(authToken)){
                return auth;
            }
        }
        throw new DataAccessException("Error: No authToken Found");
    }

    @Override
    public void deleteAuth(AuthData auth){
        AUTH_DATA.remove(auth);
    }

    @Override
    public void clear(){
        AUTH_DATA.clear();
    }
}
