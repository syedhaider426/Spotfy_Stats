package stats.errors;

import java.util.Date;

/**
 * POJO for Exceptions which is the format of the response sent to the client
 */
public class ExceptionResponse {
    private Date timestamp;

    //Message represents the user-readable error
    private String message;

    //Details represent the java error
    private String details;

    public ExceptionResponse(Date timestamp, String message, String details) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}