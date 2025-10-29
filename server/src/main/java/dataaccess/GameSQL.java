package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import service.AlreadyTakenException;
import service.DoesNotExistException;

import java.sql.Statement;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class GameSQL implements GameDao{

    public GameData createGame(String gameName) throws DataAccessException{
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
            try (var insertStmt = conn.prepareStatement(insertStatement, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, gameName);
                insertStmt.setString(2, gameJson);
                insertStmt.executeUpdate();

                try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int newGameID = generatedKeys.getInt(1);
                        return new GameData(newGameID, null, null, gameName, newChessGame);
                    } else {
                        throw new DataAccessException("Error: Failed to create new game, no ID obtained.");
                    }
                }
            }
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
                        return null;
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
        if(getGame(gameID)== null){
            throw new DoesNotExistException("Error: game does not exist");
        }

    }

    public void clear() throws DataAccessException {
        String updateStatement = "TRUNCATE GameData";
        DatabaseManager.executeUpdate(updateStatement);
    }
}
