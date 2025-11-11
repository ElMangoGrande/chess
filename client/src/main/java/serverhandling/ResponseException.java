package serverhandling;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class ResponseException extends Exception {


    public ResponseException( String message) {
        super(message);
    }

    public static String statusCode(int httpStatusCode){
        return switch (httpStatusCode){
            case 500 -> "Error: Server error";
            case 400 -> "Error: Bad request";
            case 401 -> "Error: Unauthorized Request";
            case 403 -> "Error: Already Taken";
            case 404 -> "Error: Not Found";
            default -> throw new IllegalArgumentException("Error: illegal argument");
        };
    }
}
