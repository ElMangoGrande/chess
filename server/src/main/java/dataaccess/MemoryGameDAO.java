package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import model.GameData;
import org.eclipse.jetty.http.BadMessageException;
import service.AlreadyTakenException;

import java.util.HashSet;
import java.util.Set;

public class MemoryGameDAO implements GameDao{
    private static final Set<GameData> GAME_DATA = new HashSet<>();

    @Override
    public GameData createGame(String gameName) throws DataAccessException{
        for(GameData game : GAME_DATA){
            if(game.gameName().equals(gameName)){
                throw new AlreadyTakenException("Error: gameName already exists");
            }
        }
        GameData newGame = new GameData(GAME_DATA.size()+1,null,null,gameName,new ChessGame());
        GAME_DATA.add(newGame);
        return newGame;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException{
        for(GameData game : GAME_DATA){
            if(game.gameID()== gameID){
                return game;
            }
        }
        throw new BadMessageException("Error: Game not found");
    }

    @Override
    public Set<GameData> listGames(){
        return GAME_DATA;
    }

    @Override
    public void updateGame(int gameID, String color, String username, ChessMove move) throws DataAccessException, InvalidMoveException {
        GameData game = getGame(gameID);

        if(color != null && username != null){
            if(color.equals("WHITE")){
                if(game.whiteUsername() != null){
                    throw new AlreadyTakenException("Error: Team already taken");
                }
                GameData updateGame = new GameData(gameID,username,game.blackUsername(),game.gameName(),game.game());
                GAME_DATA.remove(game);
                GAME_DATA.add(updateGame);
                return;
            }
            else if(color.equals("BLACK")){
                if(game.blackUsername() != null){
                    throw new AlreadyTakenException("Error: Team already taken");
                }
                GameData updateGame = new GameData(gameID,game.whiteUsername(),username,game.gameName(),game.game());
                GAME_DATA.remove(game);
                GAME_DATA.add(updateGame);
                return;
            }
        }else if(move != null){
            game.game().makeMove(move);
            return;
        }
        throw new BadMessageException("Error: no update specified");

    }

    @Override
    public void clear(){
        GAME_DATA.clear();
    }


}

