package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.http.BadMessageException;
import service.AlreadyTakenException;
import service.DoesNotExistException;

import java.sql.Statement;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class GameSQL implements GameDao{

    public GameData createGame(String gameName) throws DataAccessException{

        if(gameName == null){
            throw new BadMessageException("Error: game name needed");
        }
        String checkStatement = "SELECT * FROM GameData WHERE gameName = ?";

        try (Connection conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(checkStatement)) {

            preparedStatement.setString(1, gameName);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    throw new AlreadyTakenException("Error: Game already exists");
                }
            }
            ChessGame newChessGame = new ChessGame();
            Gson gson = new Gson();
            String gameJson = gson.toJson(newChessGame);

            String insertStatement = "INSERT INTO GameData (gameName, game) VALUES (?, ?)";
            int gameID = DatabaseManager.executeUpdate(insertStatement,gameName,gameJson);
            return new GameData(gameID,null,null,gameName,newChessGame);

        } catch (SQLException ex) {
            throw new DataAccessException("Error: SQL operation failed", ex);
        }
    }

    public GameData getGame(int gameID) throws DataAccessException{
        String selectStatement = "SELECT * FROM GameData WHERE gameID = ?";
        Gson gson = new Gson();
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(selectStatement)) {
                preparedStatement.setInt(1, gameID);
                try(ResultSet rs = preparedStatement.executeQuery()){
                    if(rs.next()){
                        String gameJson = rs.getString("game"); // get the JSON string
                        ChessGame chessGame = gson.fromJson(gameJson, ChessGame.class);
                        return new GameData(rs.getInt("gameID"),
                                rs.getString("whiteUsername"),
                                rs.getString("blackUsername"),
                                rs.getString("gameName"),
                                chessGame);
                    }
                    else{
                        throw new BadMessageException("Error: Bad GetGame Request");
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: SQL tables didn't create");
        }
    }


    public Set<GameData> listGames() throws DataAccessException{
        String selectStatement= "SELECT * FROM GameData";
        Gson gson = new Gson();
        Set<GameData> games = new HashSet<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(selectStatement)) {
                try(ResultSet rs = preparedStatement.executeQuery()){
                    while(rs.next()){
                        String gameJson = rs.getString("game");
                        ChessGame chessGame = gson.fromJson(gameJson, ChessGame.class);
                        GameData game = new GameData(rs.getInt("gameID"),
                                rs.getString("whiteUsername"),
                                rs.getString("blackUsername"),
                                rs.getString("gameName"),
                                chessGame);
                        games.add(game);
                    }
                    return games;
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: SQL tables didn't create");
        }

    }
    public void updateGame(int gameID, String color, String username, ChessMove move) throws DataAccessException, InvalidMoveException{
        GameData game = getGame(gameID);

        if(game== null){
            throw new DoesNotExistException("Error: game does not exist");
        }
        //check the move
        //update if valid
        Gson gson = new Gson();
        String gameJson = gson.toJson(game);

        if(username == null || color == null){
            if(move != null){
                game.game().makeMove(move);
                gameJson = gson.toJson(game.game());
                String updateStatement = "UPDATE GameData SET game = ? WHERE gameID = ?";
                DatabaseManager.executeUpdate(updateStatement,gameJson,gameID);
                return;
            }else{
                throw new BadMessageException("Error: bad update game request");
            }
        }
        if(color.equals("WHITE")) {
            if(game.whiteUsername() == null) {
                String updateStatement = "UPDATE GameData SET whiteUsername = ? WHERE gameID = ?";
                DatabaseManager.executeUpdate(updateStatement,username,gameID);
            }else{
                throw new AlreadyTakenException("Error: white player taken");
            }
        }else if(color.equals("BLACK")){
            if(game.blackUsername() == null) {
                String updateStatement = "UPDATE GameData SET blackUsername = ? WHERE gameID = ?";
                DatabaseManager.executeUpdate(updateStatement,username,gameID);
            }else{
                throw new AlreadyTakenException("Error: black player taken");
            }
        }else{
            throw new BadMessageException("Error: invalid team color");
        }
    }

    public void leaveGame(int gameID, String color) throws DataAccessException {
        if(color != null){
            if(color.equals("BLACK")){
                String updateStatement = "UPDATE GameData SET blackUsername = ? WHERE gameID = ?";
                DatabaseManager.executeUpdate(updateStatement,null,gameID);
            } else if (color.equals("WHITE")) {
                String updateStatement = "UPDATE GameData SET whiteUsername = ? WHERE gameID = ?";
                DatabaseManager.executeUpdate(updateStatement,null,gameID);
            }
        }
    }

    public void gameOver(int gameID) throws DataAccessException {
        GameData game = getGame(gameID);
        game.game().GameOver(true);
        Gson gson = new Gson();
        String updatedJson = gson.toJson(game.game());
        String updateStatement = "UPDATE GameData SET game = ? WHERE gameID = ?";
        DatabaseManager.executeUpdate(updateStatement,updatedJson,gameID);
    }

    public void clear() throws DataAccessException {
        String updateStatement = "TRUNCATE GameData";
        DatabaseManager.executeUpdate(updateStatement);
    }
}
