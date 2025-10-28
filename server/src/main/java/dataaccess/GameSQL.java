package dataaccess;

import chess.ChessMove;
import chess.InvalidMoveException;
import model.AuthData;
import model.GameData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public class GameSQL implements GameDao{
    public GameData createGame(String gameName) throws DataAccessException{

    }

    public GameData getGame(int gameID) throws DataAccessException{
        String selectStatement = "SELECT * FROM GameData WHERE gameID = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(selectStatement)) {
                preparedStatement.setInt(1, gameID);
                try(ResultSet rs = preparedStatement.executeQuery()){
                    if(rs.next()){
                        return new GameData(rs.getInt("gameID"),
                                rs.getString("whiteUsername"),
                                rs.getString("blackUsername"),
                                rs.getString("gameName"),
                                rs.getString(""));
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

    }
    public void updateGame(int gameID, String color, String username, ChessMove move) throws DataAccessException, InvalidMoveException{

    }

    public void clear() throws DataAccessException {
        String updateStatement = "TRUNCATE GameData";
        DatabaseManager.executeUpdate(updateStatement);
    }
}
