package websocket.commands;

public class JoinCommand extends UserGameCommand{
    public JoinCommand(CommandType commandType, String authToken, Integer gameID, String color) {
        super(commandType, authToken, gameID);
        this.color = color;
    }

    private String color;

    public String getColor() {
        return color;
    }
}