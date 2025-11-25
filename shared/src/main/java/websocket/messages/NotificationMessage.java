package websocket.messages;

public class NotificationMessage extends ServerMessage{
    public NotificationMessage(ServerMessageType type, String message) {
        super(type);
        this.message = message;
    }

    private String message;

    public String getNotificationMessage() {
        return message;
    }
}