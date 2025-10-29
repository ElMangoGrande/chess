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
        Set<GameData> Games = new HashSet<>();
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
                        Games.add(game);
                    }
                    return Games;
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

        if(Objects.equals(color, "WHITE")) {
            String updateStatement = "INSERT INTO GameData(gameID,whiteUsername,blackUsername,gameName,game) VALUES(?,?,?,?,?)";
            DatabaseManager.executeUpdate(updateStatement,gameID,username,game.blackUsername(),game.gameName(),gameJson);
        }else if(Objects.equals(color, "BLACK")){
            String updateStatement = "INSERT INTO GameData(gameID,whiteUsername,blackUsername,gameName,game) VALUES(?,?,?,?,?)";
            DatabaseManager.executeUpdate(updateStatement,gameID,game.whiteUsername(),username,game.gameName(),gameJson);
        }else{
            throw new DataAccessException("Error: invalid team color");
        }

    }

    public void clear() throws DataAccessException {
        String updateStatement = "TRUNCATE GameData";
        DatabaseManager.executeUpdate(updateStatement);
    }
}
