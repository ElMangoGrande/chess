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
        GameData game = getGame(gameID);
        if(game == null){
            throw new DataAccessException("Error: Game not found");
        }

        GameData updateGame = game;

        if(color != null && username != null){
            if(color.equals("WHITE")){
                updateGame = new GameData(gameID,username,game.blackUsername(),game.gameName(),game.game());
            }
            if(color.equals("BLACK")){
                updateGame = new GameData(gameID,game.whiteUsername(),username,game.gameName(),game.game());
            }
        }else if(move != null){
            ChessGame currentGame = game.game();
            currentGame.makeMove(move);
            updateGame = new GameData(gameID,game.whiteUsername(),game.whiteUsername(),game.gameName(),game.game());

        }

        GAME_DATA.remove(game);
        GAME_DATA.add(updateGame);

    }

    @Override
    public void clear(){
        GAME_DATA.clear();
    }


}

