package dataaccess;

import model.GameData;
import model.UserData;
import org.eclipse.jetty.http.BadMessageException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.AlreadyTakenException;
import service.GameService;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

class UserSQLTest {

    static UserDao userDao;

    @BeforeAll
    static void setup() throws DataAccessException {
        userDao = new UserSQL();
    }

    @Test
    void createUser() throws DataAccessException {
        userDao.clear();
        UserData userData = new UserData("Chris","Gamer","Chris@gmail.com");
        userDao.createUser(userData);
        assertNotNull(userDao.getUser("Chris"));
    }

    @Test
    void createUserFail() throws DataAccessException {
        userDao.clear();
        UserData userData = new UserData("Chris","Gamer","Chris@gmail.com");
        userDao.createUser(userData);
        assertThrows(AlreadyTakenException.class, () -> userDao.createUser(userData));
    }

    @Test
    void getUser() throws DataAccessException {
        userDao.clear();
        UserData userData = new UserData("Chris","Gamer","Chris@gmail.com");
        userDao.createUser(userData);
        UserData user = userDao.getUser("Chris");
        assertEquals("Chris",user.username());
        assertEquals("Chris@gmail.com",user.email());
    }

    @Test
    void getUserFail() throws DataAccessException {
        userDao.clear();
        UserData userData = new UserData("Chris","Gamer","Chris@gmail.com");
        userDao.createUser(userData);
        UserData user = userDao.getUser(null);
        assertEquals(null,user);
    }

    @Test
    void clear() throws DataAccessException {
        userDao.clear();
        UserData userData = new UserData("Chris","Gamer","Chris@gmail.com");
        userDao.createUser(userData);
        userDao.clear();
        userData = new UserData("Chris","Gamer","ChrisIsCool@gmail.com");
        userDao.createUser(userData);
        UserData user =userDao.getUser("Chris");
        assertEquals("ChrisIsCool@gmail.com",user.email());
    }
}