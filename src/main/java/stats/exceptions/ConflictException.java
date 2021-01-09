package stats.exceptions;

/**
 * Conflict exception is thrown when there is a 409 error i.e when the resource already exists
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}