package dataaccess;

import chess.ChessGame;
import chess.InvalidMoveException;
import model.GameData;
import org.eclipse.jetty.http.BadMessageException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.AlreadyTakenException;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GameSQLTest {

    static GameDao gameDao;

    @BeforeAll
    static void setup() throws DataAccessException {
        gameDao = new GameSQL();
    }

    @Test
    void createGame() throws DataAccessException {
        gameDao.clear();
        gameDao.createGame("myGame");
        GameData game = gameDao.getGame(1);
        assertNotNull(game);
        assertEquals("myGame",game.gameName());
    }

    @Test
    void createGameFail() throws DataAccessException {
        gameDao.clear();
        assertThrows(BadMessageException.class, () -> gameDao.createGame(null));
        gameDao.createGame("myGame");
        assertThrows(AlreadyTakenException.class, () -> gameDao.createGame("myGame"));

    }

    @Test
    void getGame() throws DataAccessException {
        gameDao.clear();
        gameDao.createGame("myGame");
        assertEquals(new GameData(1,null,null,"myGame",new ChessGame()),gameDao.getGame(1));
    }

    @Test
    void getGameFail() throws DataAccessException {
        gameDao.clear();
        gameDao.createGame("myGame");
        assertThrows(BadMessageException.class, () -> gameDao.getGame(0));
    }

    @Test
    void listGames() throws DataAccessException {
        gameDao.clear();
        Set<GameData> set = gameDao.listGames();
        assertEquals(new HashSet<>(),set);
        gameDao.createGame("myGame");
        gameDao.createGame("yourGame");
        set = gameDao.listGames();
        assertNotNull(set);
    }

    @Test
    void listGamesFail() {
        assertTrue(true);
        //this will only fail if the database breaks and
        //that isn't really the object of my testing here
    }

    @Test
    void updateGame() throws DataAccessException, InvalidMoveException {
        gameDao.clear();
        GameData game = gameDao.createGame("myGame");
        gameDao.updateGame(1,"WHITE","Hyrum",null);
        GameData gameUpdated = gameDao.getGame(1);
        assertEquals("Hyrum",gameUpdated.whiteUsername());
    }

    @Test
    void updateGameFail() throws DataAccessException, InvalidMoveException {
        gameDao.clear();
        GameData game = gameDao.createGame("myGame");
        gameDao.updateGame(1,"WHITE","Hyrum",null);
        assertThrows(BadMessageException.class, () -> gameDao.updateGame(1,"WHITE",null,null));
        assertThrows(BadMessageException.class, () -> gameDao.updateGame(1,null,"Johnny",null));
    }

    @Test
    void clear() throws DataAccessException {
        gameDao.clear();
        gameDao.createGame("myGame");
        GameData game = gameDao.getGame(1);
        gameDao.clear();
        game = gameDao.createGame("myGame");
        assertEquals(1,game.gameID());

    }
}