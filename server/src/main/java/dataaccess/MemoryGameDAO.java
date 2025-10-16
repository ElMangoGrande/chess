package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import model.GameData;

import java.util.HashSet;
import java.util.Set;

public class MemoryGameDAO implements GameDao{
    private static final Set<GameData> GAME_DATA = new HashSet<>();

    @Override
    public GameData createGame(String gameName) throws DataAccessException{
        for(GameData game : GAME_DATA){
            if(game.gameName().equals(gameName)){
                throw new DataAccessException("Error: gameName already exists");
            }
        }
        GameData newGame = new GameData(GAME_DATA.size()+1,null,null,gameName,new ChessGame());
        GAME_DATA.add(newGame);
        return newGame;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException{
        for(GameData game : GAME_DATA){
            if(game.gameId()== gameID){
                return game;
            }
        }
        throw new DataAccessException("Error: Game not found");
    }

    @Override
    public Set<GameData> listGames() throws DataAccessException{
        return GAME_DATA;
    }

    @Override
    public void updateGame(int gameID, String color, String username, ChessMove move) throws DataAccessException{
        for(GameData game : GAME_DATA){
            if(game.gameId()== gameID){
                if(color != null && username != null){
                    if(color == "White"){
                        if(move != null){
                            game.game().makeMove(move);
                        }
                        GameData updateGame = new GameData(gameID,username,game.blackUsername(),game.gameName(),game.game());
                    }
                    if(color == "Black"){

                    }
                }
                GameData updateGame = new GameData(gameID,game.whiteUsername(),game.blackUsername(),)
                GAME_DATA.remove(game);
                GAME_DATA.add(updateGame);
            }
        }
        throw new DataAccessException("Error: Game not found");
    }

    @Override
    public void clear(){
        GAME_DATA.clear();
    }


}

