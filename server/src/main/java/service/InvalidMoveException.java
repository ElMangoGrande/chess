package service;

import dataaccess.DataAccessException;

public class InvalidMoveException extends DataAccessException {
    public InvalidMoveException(String message) {
        super(message);
    }
}
