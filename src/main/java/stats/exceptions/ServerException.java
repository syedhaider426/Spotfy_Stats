package stats.exceptions;

/**
 * Server exception is thrown when there is a 500 error i.e when resource is unable to process the request due to a
 * server-side issue (such as the DynamoDB Database being down
 */
public class ServerException extends RuntimeException {
    public ServerException(String message) {
        super(message);
    }
}