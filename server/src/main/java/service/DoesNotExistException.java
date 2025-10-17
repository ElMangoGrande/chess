package service;

import dataaccess.DataAccessException;

public class DoesNotExistException extends DataAccessException {
    public DoesNotExistException(String message) {
        super(message);
    }
}
