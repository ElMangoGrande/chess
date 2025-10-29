package dataaccess;

import io.javalin.http.UnauthorizedResponse;
import model.AuthData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.AlreadyTakenException;

import static org.junit.jupiter.api.Assertions.*;

class AuthSQLTest {

    static AuthDao authDao;

    @BeforeAll
    static void setup() throws DataAccessException {
        authDao = new AuthSQL();
    }

    @Test
    void createAuth() throws DataAccessException {
        authDao.clear();
        authDao.createAuth(new AuthData("Chris","gamer"));
        assertNotNull(authDao.getAuth("gamer"));
    }

    @Test
    void createAuthFail() throws DataAccessException {
        authDao.clear();
        authDao.createAuth(new AuthData("Chris","gamer"));
        assertThrows(AlreadyTakenException.class, () -> authDao.createAuth(new AuthData("Chris","gamer")));
    }

    @Test
    void getAuth() throws DataAccessException {
        authDao.clear();
        authDao.createAuth(new AuthData("Chris","gamer"));
        AuthData auth = authDao.getAuth("gamer");
        assertNotNull(auth);
    }

    @Test
    void getAuthFail() throws DataAccessException {
        authDao.clear();
        authDao.createAuth(new AuthData("Chris","gamer"));
        assertNull(authDao.getAuth(null));
    }

    @Test
    void deleteAuth() throws DataAccessException {
        authDao.clear();
        authDao.createAuth(new AuthData("Chris","gamer"));
        AuthData auth = authDao.getAuth("gamer");
        authDao.deleteAuth(auth);
        assertNull(authDao.getAuth("gamer"));
    }

    @Test
    void deleteAuthFail() throws DataAccessException {
        authDao.clear();
        authDao.createAuth(new AuthData("Chris","gamer"));
        AuthData auth = authDao.getAuth("gamer");
        assertThrows(UnauthorizedResponse.class, () -> authDao.deleteAuth(null));
    }

    @Test
    void clear() throws DataAccessException {
        authDao.clear();
        authDao.createAuth(new AuthData("Chris","gamer"));
        authDao.clear();
        authDao.createAuth(new AuthData("Chris","gamer420"));
        AuthData auth = authDao.getAuth("gamer420");
        assertEquals(new AuthData("Chris","gamer420"),auth);
    }
}