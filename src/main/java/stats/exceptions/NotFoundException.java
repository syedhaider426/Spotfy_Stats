package stats.exceptions;

/**
 * Not Found exception is thrown when there is a 404 error i.e when the resource requested cannot be found
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}