package stats.errors;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import stats.exceptions.ConflictException;
import stats.exceptions.NotFoundException;
import stats.exceptions.ServerException;

import java.util.Date;

/**
 * Exceptions are controlled through this class RestExceptionHandler. It is intended
 * to intercept the logic in the components package and apply common errors to them.
 * Using RestControllerAdvice allows Spring to share these Exceptions among Controller
 * components.
 * <p>
 * Useful source: https://bezkoder.com/spring-boot-restcontrolleradvice/
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {


    /**
     * This is the generic exception handler that is thrown.
     *
     * @param ex  exception that is thrown
     * @param req endpoint the client requested
     * @return 500 response to the client
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public final ExceptionResponse handleAllExceptions(Exception ex, WebRequest req) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                new Date(),
                ex.getMessage(),
                req.getDescription(false)
        );
        return exceptionResponse;
    }


    /**
     * This is the ConflictException handler that is thrown for 409 errors.
     *
     * @param ex  exception that is thrown
     * @param req endpoint the client requested
     * @return 409 response to the client
     */
    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public final ExceptionResponse handleConflictException(
            ConflictException ex, WebRequest req) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                new Date(),
                ex.getMessage(),
                req.getDescription(false)
        );
        return exceptionResponse;
    }

    /**
     * This is the NotFoundException handler that is thrown for 404 errors.
     *
     * @param ex  exception that is thrown
     * @param req endpoint the client requested
     * @return 404 response to the client
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public final ExceptionResponse handleNotFoundException(
            NotFoundException ex, WebRequest req) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                new Date(),
                ex.getMessage(),
                req.getDescription(false)
        );
        return exceptionResponse;
    }

    /**
     * This is the ServerException handler that is thrown for 500 errors.
     *
     * @param ex  exception that is thrown
     * @param req endpoint the client requested
     * @return 500 response to the client
     */
    @ExceptionHandler(ServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public final ExceptionResponse handleServerException(
            ServerException ex, WebRequest req) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                new Date(),
                ex.getMessage(),
                req.getDescription(false)
        );
        return exceptionResponse;
    }

}
