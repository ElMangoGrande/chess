package websocket.messages;

public class ErrorMessage extends ServerMessage{
    public ErrorMessage(ServerMessageType type, String message) {
        super(type);
        errorMessage = message;
    }

    private String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }
}
