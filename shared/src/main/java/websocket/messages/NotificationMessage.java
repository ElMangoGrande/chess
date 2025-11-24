package websocket.messages;

public class NotificationMessage extends ServerMessage{
    public NotificationMessage(ServerMessageType type, String message) {
        super(type);
        notificationMessage = message;
    }

    private String notificationMessage;

    public String getNotificationMessage() {
        return notificationMessage;
    }
}